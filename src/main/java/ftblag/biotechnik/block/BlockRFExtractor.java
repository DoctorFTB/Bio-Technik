package ftblag.biotechnik.block;

import ftblag.biotechnik.BioTechnik;
import ftblag.biotechnik.config.BTConfigParser;
import ftblag.biotechnik.item.ItemRFCollector;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockRFExtractor extends Block {

    public BlockRFExtractor(Properties p_i48440_1_) {
        super(p_i48440_1_);
        setRegistryName(BioTechnik.MODID, "rfextractor");
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult blockRTS) {
        if (!world.isRemote && hand == Hand.MAIN_HAND) {
            TileEntity te1 = world.getTileEntity(pos);
            if (te1 == null || !(te1 instanceof TileEntityRFExtractor))
                return true;
            TileEntityRFExtractor te = (TileEntityRFExtractor) te1;
            ItemStack is = player.getHeldItem(hand);
            if (!is.isEmpty() && is.getItem() == BTConfigParser.getExtractorToggle()) {
                if (!te.collectOrbs) {
                    te.collectOrbs = true;
                    player.sendMessage(new StringTextComponent("Auto collect activated."));
                } else {
                    te.collectOrbs = false;
                    player.sendMessage(new StringTextComponent("Auto collect deactivated."));
                }
            } else if (!is.isEmpty() && is.getItem() instanceof ItemRFCollector) {
                ItemRFCollector collector = (ItemRFCollector) is.getItem();
                int store = collector.getRF(is);
                if (store > 0) {
                    int rem = te.storage.setEnergy(Math.min(te.storage.getMaxEnergyStored(), te.storage.getEnergyStored() + store));
                    collector.remRF(is, rem);
                }
                player.sendMessage(new StringTextComponent("Capacity energy: " + te.storage.getEnergyStored() + "/" + te.storage.getMaxEnergyStored()));
            } else
                player.sendMessage(new StringTextComponent("Capacity energy: " + te.storage.getEnergyStored() + "/" + te.storage.getMaxEnergyStored()));
        }
        return true;
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
