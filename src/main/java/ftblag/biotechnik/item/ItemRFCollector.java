package ftblag.biotechnik.item;

import java.util.List;

import ftblag.biotechnik.BTUtils;
import ftblag.biotechnik.BioTechnik;
import ftblag.biotechnik.tab.BTTab;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by FTB_lag.
 */
public class ItemRFCollector extends Item {

    public ItemRFCollector() {
        setRegistryName(BioTechnik.MODID, "rfcollector");
        setUnlocalizedName(BioTechnik.MODID + "." + "rfcollector");
        setCreativeTab(BTTab.TAB);
        setMaxStackSize(1);
        ForgeRegistries.ITEMS.register(this);
    }

    public int addRF(ItemStack is, int amount, boolean isFake) {
        int store = getRF(is) + amount;
        int leftover = Math.max(store - getMaxRF(is), 0);
        if (!isFake)
            is.setTagInfo("rf", new NBTTagInt(Math.min(store, getMaxRF(is))));
        return leftover;
    }

    public int getRF(ItemStack is) {
        return BTUtils.isOk(is) && is.hasTagCompound() && is.getTagCompound().hasKey("rf")
                ? is.getTagCompound().getInteger("rf")
                : 0;
    }

    public int getMaxRF(ItemStack is) {
        return 100 * 100;
    }

    public void remRF(ItemStack is, int amount) {
        is.setTagInfo("rf", new NBTTagInt(getRF(is) - amount));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add("Capacity: " + getRF(stack));
    }
}