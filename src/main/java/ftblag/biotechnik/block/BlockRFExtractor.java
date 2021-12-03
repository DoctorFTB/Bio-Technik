package ftblag.biotechnik.block;

import ftblag.biotechnik.BioTechnik;
import ftblag.biotechnik.config.BTConfigParser;
import ftblag.biotechnik.item.ItemRFCollector;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
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
        setRegistryName(BioTechnik.MODID, "rfextractor");
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockRTS) {
        if (!world.isClientSide && hand == InteractionHand.MAIN_HAND) {
            BlockEntity te1 = world.getBlockEntity(pos);
            if (te1 == null || !(te1 instanceof TileEntityRFExtractor))
                return InteractionResult.SUCCESS;
            TileEntityRFExtractor te = (TileEntityRFExtractor) te1;
            ItemStack is = player.getItemInHand(hand);
            if (!is.isEmpty() && is.getItem() == BTConfigParser.getExtractorToggle()) {
                if (!te.collectOrbs) {
                    te.collectOrbs = true;
                    player.sendMessage(new TextComponent("Auto collect activated."), Util.NIL_UUID);
                } else {
                    te.collectOrbs = false;
                    player.sendMessage(new TextComponent("Auto collect deactivated."), Util.NIL_UUID);
                }
            } else if (!is.isEmpty() && is.getItem() instanceof ItemRFCollector) {
                ItemRFCollector collector = (ItemRFCollector) is.getItem();
                int store = collector.getRF(is);
                if (store > 0) {
                    int rem = te.storage.setEnergy(Math.min(te.storage.getMaxEnergyStored(), te.storage.getEnergyStored() + store));
                    collector.remRF(is, rem);
                }
                player.sendMessage(new TextComponent("Capacity energy: " + te.storage.getEnergyStored() + "/" + te.storage.getMaxEnergyStored()), Util.NIL_UUID);
            } else
                player.sendMessage(new TextComponent("Capacity energy: " + te.storage.getEnergyStored() + "/" + te.storage.getMaxEnergyStored()), Util.NIL_UUID);
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type == BioTechnik.extractorType) {
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
