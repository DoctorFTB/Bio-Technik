package ftblag.biotechnik.api;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

/**
 * Created by FTB_lag.
 */
public class BTConfigsAPI {

    public static ArrayList<String> blackListMobs = new ArrayList<>();
    public static HashMap<String, Integer> specificDrops = new HashMap<>();

    public static void addToBlackListMob(Entity entity) {
        blackListMobs.add(EntityRegistry.getEntry(entity.getClass()).getRegistryName().toString());
    }

    public static void addToBlackListMob(ResourceLocation rl) {
        blackListMobs.add(rl.toString());
    }

    public static void addSpecificDrops(Entity entity, int amount) {
        specificDrops.put(EntityRegistry.getEntry(entity.getClass()).getRegistryName().toString(), amount);
    }

    public static void addSpecificDrops(ResourceLocation rl, int amount) {
        specificDrops.put(rl.toString(), amount);
    }
}