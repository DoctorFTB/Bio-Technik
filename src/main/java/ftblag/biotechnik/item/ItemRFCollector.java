package ftblag.biotechnik.item;

import ftblag.biotechnik.BioTechnik;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemRFCollector extends Item {

    public ItemRFCollector(Properties props) {
        super(props);
        setRegistryName(BioTechnik.MODID, "rfcollector");
    }

    public int addRF(ItemStack is, int amount, boolean isFake) {
        int store = getRF(is) + amount;
        int leftover = Math.max(store - getMaxRF(is), 0);
        if (!isFake)
            is.setTagInfo("rf", new IntNBT(Math.min(store, getMaxRF(is))));
        return leftover;
    }

    public int getRF(ItemStack is) {
        return !is.isEmpty() && is.hasTag() && is.getTag().contains("rf")
                ? is.getTag().getInt("rf")
                : 0;
    }

    public int getMaxRF(ItemStack is) {
        return 100 * 100;
    }

    public void remRF(ItemStack is, int amount) {
        is.setTagInfo("rf", new IntNBT(getRF(is) - amount));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        tooltip.add(new StringTextComponent("Capacity: " + getRF(stack)));
    }
}
