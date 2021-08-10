package ftblag.biotechnik.block;

import ftblag.biotechnik.BioTechnik;
import ftblag.biotechnik.config.BTConfigParser;
import ftblag.biotechnik.item.ItemRFCollector;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockRFExtractor extends Block {

    public BlockRFExtractor(Properties properties) {
        super(properties);
        setRegistryName(BioTechnik.MODID, "rfextractor");
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult blockRTS) {
        if (!world.isClientSide && hand == Hand.MAIN_HAND) {
            TileEntity te1 = world.getBlockEntity(pos);
            if (te1 == null || !(te1 instanceof TileEntityRFExtractor))
                return ActionResultType.SUCCESS;
            TileEntityRFExtractor te = (TileEntityRFExtractor) te1;
            ItemStack is = player.getItemInHand(hand);
            if (!is.isEmpty() && is.getItem() == BTConfigParser.getExtractorToggle()) {
                if (!te.collectOrbs) {
                    te.collectOrbs = true;
                    player.sendMessage(new StringTextComponent("Auto collect activated."), Util.NIL_UUID);
                } else {
                    te.collectOrbs = false;
                    player.sendMessage(new StringTextComponent("Auto collect deactivated."), Util.NIL_UUID);
                }
            } else if (!is.isEmpty() && is.getItem() instanceof ItemRFCollector) {
                ItemRFCollector collector = (ItemRFCollector) is.getItem();
                int store = collector.getRF(is);
                if (store > 0) {
                    int rem = te.storage.setEnergy(Math.min(te.storage.getMaxEnergyStored(), te.storage.getEnergyStored() + store));
                    collector.remRF(is, rem);
                }
                player.sendMessage(new StringTextComponent("Capacity energy: " + te.storage.getEnergyStored() + "/" + te.storage.getMaxEnergyStored()), Util.NIL_UUID);
            } else
                player.sendMessage(new StringTextComponent("Capacity energy: " + te.storage.getEnergyStored() + "/" + te.storage.getMaxEnergyStored()), Util.NIL_UUID);
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEntityRFExtractor();
    }
}
