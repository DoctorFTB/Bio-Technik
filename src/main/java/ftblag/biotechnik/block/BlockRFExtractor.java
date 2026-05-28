package ftblag.biotechnik.block;

import ftblag.biotechnik.BTRegistry;
import ftblag.biotechnik.config.BTConfigParser;
import ftblag.biotechnik.item.ItemRFCollector;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class BlockRFExtractor extends Block implements EntityBlock {

    public BlockRFExtractor(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockRTS) {
        if (world.isClientSide() || hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity te1 = world.getBlockEntity(pos);
        if (te1 == null || !(te1 instanceof TileEntityRFExtractor))
            return InteractionResult.CONSUME;
        TileEntityRFExtractor te = (TileEntityRFExtractor) te1;
        ItemStack is = player.getItemInHand(hand);

        if (!is.isEmpty() && is.getItem() == BTConfigParser.getExtractorToggle()) {
            if (!te.collectOrbs) {
                te.collectOrbs = true;
                te.setChanged();
                player.sendOverlayMessage(Component.literal("Auto collect activated."));
            } else {
                te.collectOrbs = false;
                te.setChanged();
                player.sendOverlayMessage(Component.literal("Auto collect deactivated."));
            }

            return InteractionResult.SUCCESS;
        } else if (!is.isEmpty() && is.getItem() instanceof ItemRFCollector) {
            ItemRFCollector collector = (ItemRFCollector) is.getItem();
            int store = collector.getRF(is);

            if (store > 0) {
                int rem = te.storage.setEnergy(Math.min((int) te.storage.getCapacityAsLong(), (int) te.storage.getAmountAsLong() + store));
                collector.remRF(is, rem);
                te.setChanged();
            }

            player.sendOverlayMessage(Component.literal("Capacity energy: " + te.storage.getAmountAsLong() + "/" + te.storage.getCapacityAsLong()));

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult blockRTS) {
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity te1 = world.getBlockEntity(pos);
        if (te1 == null || !(te1 instanceof TileEntityRFExtractor))
            return InteractionResult.CONSUME;
        TileEntityRFExtractor te = (TileEntityRFExtractor) te1;

        player.sendOverlayMessage(Component.literal("Capacity energy: " + te.storage.getAmountAsLong() + "/" + te.storage.getCapacityAsLong()));

        return InteractionResult.CONSUME;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (!level.isClientSide() && type == BTRegistry.EXTRACTOR_TYPE.get()) {
            BlockEntityTicker<TileEntityRFExtractor> tick = TileEntityRFExtractor::tick;
            return (BlockEntityTicker<T>) tick;
        }
        return null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityRFExtractor(pos, state);
    }
}
