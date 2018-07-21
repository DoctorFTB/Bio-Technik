package ftblag.biotechnik.block;

import cofh.redstoneflux.api.IEnergyConnection;
import cofh.redstoneflux.api.IEnergyHandler;
import cofh.redstoneflux.api.IEnergyReceiver;
import ftblag.biotechnik.config.BTConfigParser;
import ftblag.biotechnik.entity.EntityRFOrb;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Loader;

import java.util.List;

/**
 * Created by FTB_lag.
 */
public class TileEntityRFExtractor extends TileEntity implements ITickable {

    public CustomEnergyStorage storage = new CustomEnergyStorage(1000000, 0, 1000, 0);
    public boolean collectOrbs = false;

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        storage.setEnergy(compound.hasKey("rf") ? compound.getInteger("rf") : 0);
        collectOrbs = compound.getBoolean("collectOrbs");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("rf", storage.getEnergyStored());
        compound.setBoolean("collectOrbs", collectOrbs);
        return compound;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return !oldState.getBlock().isAssociatedBlock(newSate.getBlock());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, -1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        handleUpdateTag(pkt.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        readFromNBT(tag);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY ? (T) storage : super.getCapability(capability, facing);
    }

    @Override
    public void update() {
        if (!world.isRemote) {
            if (collectOrbs)
                getOrbs();
            if (storage.getEnergyStored() > 0)
                sendOutEnergy(storage.getEnergyStored());
        }
    }

    private void getOrbs() {
        List<EntityRFOrb> orbs = getWorld().getEntitiesWithinAABB(EntityRFOrb.class, new AxisAlignedBB(getPos()).grow(BTConfigParser.getRadius()));
        if (!orbs.isEmpty())
            for (EntityRFOrb orb : orbs)
                if (!orb.isDead && orb.rfValue > 0) {
                    int rem = storage.setEnergy(Math.min(storage.getMaxEnergyStored(), storage.getEnergyStored() + orb.rfValue));
                    orb.rfValue -= rem;
                    if (orb.rfValue <= 0)
                        orb.setDead();
                }
    }

    private void sendOutEnergy(int energyStored) {
        int energyExtracted = 0;

        for (EnumFacing face : EnumFacing.VALUES) {
            BlockPos pos = getPos().offset(face);
            TileEntity te = getWorld().getTileEntity(pos);
            EnumFacing opposite = face.getOpposite();
            if (isTECorrect(te, opposite)) {
                int rfPerTick = 100;
                int received = 0;

                // Do min with 'long' and cast to int afterwards
                int rfToGive = Math.min(rfPerTick, energyStored);

                if (Loader.isModLoaded("redstoneflux") && te instanceof IEnergyConnection) {
                    if (((IEnergyConnection) te).canConnectEnergy(opposite)) {

                        received = ((IEnergyReceiver) te).receiveEnergy(opposite, rfToGive, false);
                    } else
                        received = 0;
                } else {
                    // Forge unit
                    IEnergyStorage capability = te.getCapability(CapabilityEnergy.ENERGY, opposite);
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

    private boolean isTECorrect(TileEntity te, EnumFacing side) {
        return te == null ? false
                : Loader.isModLoaded("redstoneflux") && te instanceof IEnergyHandler ? true
                        : te.hasCapability(CapabilityEnergy.ENERGY, side);
    }
}