package ftblag.biotechnik.block;

import ftblag.biotechnik.BTUtils;
import ftblag.biotechnik.BioTechnik;
import ftblag.biotechnik.item.ItemRFCollector;
import ftblag.biotechnik.tab.BTTab;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by FTB_lag.
 */
public class BlockRFExtractor extends Block implements ITileEntityProvider {

    public BlockRFExtractor() {
        super(Material.ROCK);
        setRegistryName(BioTechnik.MODID, "rfextractor");
        setUnlocalizedName(BioTechnik.MODID + "." + "rfextractor");
        setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 2);
        setHardness(1F);
        setResistance(10F);
        setCreativeTab(BTTab.TAB);
        ForgeRegistries.BLOCKS.register(this);
        ForgeRegistries.ITEMS.register(new ItemBlock(this).setRegistryName(getRegistryName()));
        GameRegistry.registerTileEntity(TileEntityRFExtractor.class,
                BioTechnik.MODID + ":rfextractor");
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
            EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote && hand == EnumHand.MAIN_HAND) {
            TileEntity te1 = world.getTileEntity(pos);
            if (te1 == null || !(te1 instanceof TileEntityRFExtractor))
                return true;
            TileEntityRFExtractor te = (TileEntityRFExtractor) te1;
            ItemStack is = player.getHeldItem(hand);
            if (BTUtils.isOk(is) && is.getItem() == Items.NETHER_STAR) {
                if (!te.collectOrbs) {
                    te.collectOrbs = true;
                    player.sendMessage(new TextComponentString("Auto collect activated."));
                } else if (te.collectOrbs) {
                    te.collectOrbs = false;
                    player.sendMessage(new TextComponentString("Auto collect deactivated."));
                }
            } else if (BTUtils.isOk(is) && is.getItem() instanceof ItemRFCollector) {
                ItemRFCollector collector = (ItemRFCollector) is.getItem();
                int store = collector.getRF(is);
                if (store > 0) {
                    int rem = te.storage.setEnergy(Math.min(te.storage.getMaxEnergyStored(), te.storage.getEnergyStored() + store));
                    collector.remRF(is, rem);
                }
                player.sendMessage(new TextComponentString(
                        "Capacity energy: " + te.storage.getEnergyStored() + "/" + te.storage.getMaxEnergyStored()));
            } else
                player.sendMessage(new TextComponentString(
                        "Capacity energy: " + te.storage.getEnergyStored() + "/" + te.storage.getMaxEnergyStored()));
        }
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityRFExtractor();
    }
}