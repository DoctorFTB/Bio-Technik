package ftblag.biotechnik.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;

public class BTConfigsAPI {

    public static ArrayList<String> blackListMobs = new ArrayList<>();
    public static HashMap<String, Integer> specificDrops = new HashMap<>();

    public static void addToBlackListMob(Entity entity) {
        addToBlackListMob(entity.getType());
    }

    public static void addToBlackListMob(EntityType<?> entityType) {
        addToBlackListMob(ForgeRegistries.ENTITIES.getKey(entityType));
    }

    public static void addToBlackListMob(ResourceLocation rl) {
        blackListMobs.add(rl.toString());
    }

    public static void addSpecificDrops(Entity entity, int amount) {
        addSpecificDrops(entity.getType(), amount);
    }

    public static void addSpecificDrops(EntityType<?> entityType, int amount) {
        addSpecificDrops(ForgeRegistries.ENTITIES.getKey(entityType), amount);
    }

    public static void addSpecificDrops(ResourceLocation rl, int amount) {
        specificDrops.put(rl.toString(), amount);
    }
}
