package ftblag.biotechnik.block;

import ftblag.biotechnik.BioTechnik;
import ftblag.biotechnik.config.BTConfigParser;
import ftblag.biotechnik.entity.EntityRFOrb;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TileEntityRFExtractor extends TileEntity implements ITickableTileEntity {

    public CustomEnergyStorage storage = new CustomEnergyStorage(1000000, 0, 1000);
    public boolean collectOrbs = false;
    private final LazyOptional<IEnergyStorage> holder = LazyOptional.of(() -> storage);

    public TileEntityRFExtractor() {
        super(BioTechnik.extractorType);
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);
        storage.setEnergy(tag.contains("rf") ? tag.getInt("rf") : 0);
        collectOrbs = tag.getBoolean("collectOrbs");
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        tag.putInt("rf", storage.getEnergyStored());
        tag.putBoolean("collectOrbs", collectOrbs);
        return super.write(tag);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == CapabilityEnergy.ENERGY ? holder.cast() : super.getCapability(cap, side);
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            if (collectOrbs) {
                getOrbs();
            }
            if (storage.getEnergyStored() > 0) {
                sendOutEnergy(storage.getEnergyStored());
            }
        }
    }

    private void getOrbs() {
        List<EntityRFOrb> orbs = getWorld().getEntitiesWithinAABB(EntityRFOrb.class, new AxisAlignedBB(getPos()).grow(BTConfigParser.getRadius()));
        if (!orbs.isEmpty())
            for (EntityRFOrb orb : orbs)
                if (orb.isAlive() && orb.rfValue > 0) {
                    int rem = storage.setEnergy(Math.min(storage.getMaxEnergyStored(), storage.getEnergyStored() + orb.rfValue));
                    orb.rfValue -= rem;
                    if (orb.rfValue <= 0)
                        orb.remove();
                }
    }

    private void sendOutEnergy(int energyStored) {
        int energyExtracted = 0;

        for (Direction face : Direction.values()) {
            BlockPos pos = getPos().offset(face);
            TileEntity te = getWorld().getTileEntity(pos);
            Direction opposite = face.getOpposite();

            if (te == null) {
                continue;
            }

            boolean rf = /*Loader.isModLoaded("redstoneflux") && te instanceof IEnergyConnection)*/ false;
            LazyOptional<IEnergyStorage> cap = te.getCapability(CapabilityEnergy.ENERGY, opposite);
            if (rf || cap.isPresent()) {
                int rfPerTick = 100;
                int received = 0;

                // Do min with 'long' and cast to int afterwards
                int rfToGive = Math.min(rfPerTick, energyStored);

                if (rf) {
//                if (Loader.isModLoaded("redstoneflux") && te instanceof IEnergyConnection) {
//                    if (((IEnergyConnection) te).canConnectEnergy(opposite)) {
//
//                        received = ((IEnergyReceiver) te).receiveEnergy(opposite, rfToGive, false);
//                    } else
//                        received = 0;
                } else {
                    // Forge unit
                    IEnergyStorage capability = cap.orElse(null);
                    if (capability.canReceive())
                        received = capability.receiveEnergy(rfToGive, false);
                }

                energyStored -= received;
                energyExtracted += received;
                if (energyStored <= 0)
                    break;
            }
        }

        if (energyExtracted > 0)
            storage.setEnergy(storage.getEnergyStored() - energyExtracted);
    }
}
