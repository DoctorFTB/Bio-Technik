package ftblag.biotechnik;

import net.minecraft.item.ItemStack;

/**
 * Created by FTB_lag.
 */
public class BTUtils {

    public static boolean isOk(ItemStack is) {
        return is != null && !is.isEmpty();
    }
}