package ftblag.biotechnik.item;

import ftblag.biotechnik.BTRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.List;
import java.util.function.Consumer;

public class ItemRFCollector extends Item {

    public ItemRFCollector(Properties props) {
        super(props);
    }

    public int addRF(ItemStack is, int amount, boolean isFake) {
        int store = getRF(is) + amount;
        int leftover = Math.max(store - getMaxRF(is), 0);
        if (!isFake)
            is.set(BTRegistry.RF, Math.min(store, getMaxRF(is)));
        return leftover;
    }

    public int getRF(ItemStack is) {
        return !is.isEmpty() && is.has(BTRegistry.RF)
                ? is.get(BTRegistry.RF)
                : 0;
    }

    public int getMaxRF(ItemStack is) {
        return 100 * 100;
    }

    public void remRF(ItemStack is, int amount) {
        is.set(BTRegistry.RF, getRF(is) - amount);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.literal("Capacity: " + getRF(stack)));
    }
}
