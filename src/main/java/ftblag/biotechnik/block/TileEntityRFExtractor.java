package ftblag.biotechnik.block;

import ftblag.biotechnik.BTRegistry;
import ftblag.biotechnik.config.BTConfigParser;
import ftblag.biotechnik.entity.EntityRFOrb;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TileEntityRFExtractor extends BlockEntity {

    public CustomEnergyStorage storage = new CustomEnergyStorage(1000000, 0, 1000);
    public boolean collectOrbs = false;
    private final LazyOptional<IEnergyStorage> holder = LazyOptional.of(() -> storage);

    public TileEntityRFExtractor(BlockPos pos, BlockState state) {
        super(BTRegistry.EXTRACTOR_TYPE.get(), pos, state);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        storage.setEnergy(tag.contains("rf") ? tag.getInt("rf") : 0);
        collectOrbs = tag.getBoolean("collectOrbs");
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("rf", storage.getEnergyStored());
        tag.putBoolean("collectOrbs", collectOrbs);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == ForgeCapabilities.ENERGY ? holder.cast() : super.getCapability(cap, side);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TileEntityRFExtractor entity) {
        if (!level.isClientSide) {
            if (entity.collectOrbs) {
                entity.getOrbs();
            }
            if (entity.storage.getEnergyStored() > 0) {
                entity.sendOutEnergy(entity.storage.getEnergyStored());
            }
        }
    }

    private void getOrbs() {
        List<EntityRFOrb> orbs = getLevel().getEntitiesOfClass(EntityRFOrb.class, new AABB(worldPosition).inflate(BTConfigParser.getRadius()));
        if (!orbs.isEmpty())
            for (EntityRFOrb orb : orbs)
                if (orb.isAlive() && orb.rfValue > 0) {
                    int rem = storage.setEnergy(Math.min(storage.getMaxEnergyStored(), storage.getEnergyStored() + orb.rfValue));
                    orb.rfValue -= rem;
                    if (orb.rfValue <= 0)
                        orb.discard();
                    this.setChanged();
                }
    }

    private void sendOutEnergy(int energyStored) {
        int energyExtracted = 0;

        for (Direction face : Direction.values()) {
            BlockPos pos = worldPosition.relative(face);
            BlockEntity te = getLevel().getBlockEntity(pos);
            Direction opposite = face.getOpposite();

            if (te == null) {
                continue;
            }

            boolean rf = /*Loader.isModLoaded("redstoneflux") && te instanceof IEnergyConnection)*/ false;
            LazyOptional<IEnergyStorage> cap = te.getCapability(ForgeCapabilities.ENERGY, opposite);
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
                    if (capability.canReceive()) {
                        received = capability.receiveEnergy(rfToGive, false);
                        this.setChanged();
                    }
                }

                energyStored -= received;
                energyExtracted += received;
                if (energyStored <= 0)
                    break;
            }
        }

        if (energyExtracted > 0) {
            storage.setEnergy(storage.getEnergyStored() - energyExtracted);
            this.setChanged();
        }
    }
}
