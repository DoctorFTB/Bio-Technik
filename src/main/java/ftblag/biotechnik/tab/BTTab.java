package ftblag.biotechnik.tab;

import ftblag.biotechnik.BioTechnik;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class BTTab extends CreativeTabs {

    public static final BTTab TAB = new BTTab();

    public BTTab() {
        super(BioTechnik.MODID);
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(BioTechnik.collector);
    }
}