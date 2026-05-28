package ftblag.biotechnik.block;

import ftblag.biotechnik.BTRegistry;
import ftblag.biotechnik.config.BTConfigParser;
import ftblag.biotechnik.entity.EntityRFOrb;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.List;

public class TileEntityRFExtractor extends BlockEntity {

    public CustomEnergyStorage storage = new CustomEnergyStorage(1000000, 0, 1000);
    public boolean collectOrbs = false;

    public TileEntityRFExtractor(BlockPos pos, BlockState state) {
        super(BTRegistry.EXTRACTOR_TYPE.get(), pos, state);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        storage.deserialize(input);
        collectOrbs = input.getBooleanOr("collectOrbs", this.collectOrbs);
        super.loadAdditional(input);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        storage.serialize(output);
        output.putBoolean("collectOrbs", collectOrbs);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TileEntityRFExtractor entity) {
        if (!level.isClientSide()) {
            if (entity.collectOrbs) {
                entity.getOrbs();
            }
            if (entity.storage.getAmountAsInt() > 0) {
                entity.sendOutEnergy(entity.storage.getAmountAsInt());
            }
        }
    }

    private void getOrbs() {
        List<EntityRFOrb> orbs = getLevel().getEntitiesOfClass(EntityRFOrb.class, new AABB(worldPosition).inflate(BTConfigParser.getRadius()));
        if (!orbs.isEmpty())
            for (EntityRFOrb orb : orbs)
                if (orb.isAlive() && orb.rfValue > 0) {
                    int rem = storage.setEnergy(Math.min(storage.getCapacityAsInt(), storage.getAmountAsInt() + orb.rfValue));
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

            EnergyHandler capability = getLevel().getCapability(Capabilities.Energy.BLOCK, pos, opposite);
            if (capability != null) {
                try (Transaction tx = Transaction.openRoot()) {
                    int rfPerTick = 100;
                    int rfToGive = Math.min(rfPerTick, energyStored);

                    int received = capability.insert(rfToGive, tx);
                    if (received == 0) {
                      continue;
                    }

                    energyStored -= received;
                    energyExtracted += received;
                    if (energyStored <= 0)
                        break;

                    this.setChanged();
                    tx.commit();
                }
            }
        }

        if (energyExtracted > 0) {
            storage.setEnergy(storage.getAmountAsInt() - energyExtracted);
            this.setChanged();
        }
    }
}
