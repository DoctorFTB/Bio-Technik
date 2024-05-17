package ftblag.biotechnik.block;

import ftblag.biotechnik.BTRegistry;
import ftblag.biotechnik.config.BTConfigParser;
import ftblag.biotechnik.entity.EntityRFOrb;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.List;

public class TileEntityRFExtractor extends BlockEntity {

    public CustomEnergyStorage storage = new CustomEnergyStorage(1000000, 0, 1000);
    public boolean collectOrbs = false;

    public TileEntityRFExtractor(BlockPos pos, BlockState state) {
        super(BTRegistry.EXTRACTOR_TYPE.get(), pos, state);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        storage.setEnergy(tag.contains("rf") ? tag.getInt("rf") : 0);
        collectOrbs = tag.getBoolean("collectOrbs");
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("rf", storage.getEnergyStored());
        tag.putBoolean("collectOrbs", collectOrbs);
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

            IEnergyStorage capability = getLevel().getCapability(Capabilities.EnergyStorage.BLOCK, pos, opposite);
            if (capability != null && capability.canReceive()) {
                int rfPerTick = 100;
                int received = 0;

                // Do min with 'long' and cast to int afterwards
                int rfToGive = Math.min(rfPerTick, energyStored);

                // NeoForge unit
                received = capability.receiveEnergy(rfToGive, false);
                this.setChanged();

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
