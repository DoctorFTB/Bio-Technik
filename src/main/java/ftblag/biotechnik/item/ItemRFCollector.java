package ftblag.biotechnik.item;

import ftblag.biotechnik.BioTechnik;
import net.minecraft.nbt.IntTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

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
            is.addTagElement("rf", IntTag.valueOf(Math.min(store, getMaxRF(is))));
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
        is.addTagElement("rf", IntTag.valueOf(getRF(is) - amount));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(new TextComponent("Capacity: " + getRF(stack)));
    }
}
