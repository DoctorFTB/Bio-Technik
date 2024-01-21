package ftblag.biotechnik.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import ftblag.biotechnik.BioTechnik;
import ftblag.biotechnik.api.BTConfigsAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.util.LoaderException;
import net.minecraftforge.registries.ForgeRegistries;

public class BTConfigParser {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static BTConfigGSON cfg;
    private static Item extractorToggle;

    public static void parseFile(File file) {
        if (!file.exists())
            try {
                createDefault(file);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        try {
            cfg = new Gson().fromJson(new BufferedReader(new FileReader(file)), new TypeToken<BTConfigGSON>() {
            }.getType());
        } catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
            e.printStackTrace();
            BioTechnik.log.error("Read file is error! Reset to default!");
            try {
                createDefault(file);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        if (cfg.extractorToggleItem == null || cfg.extractorToggleItem.length() == 0) {
            extractorToggle = Items.NETHER_STAR;
        } else {
            ResourceLocation rlItem = new ResourceLocation(cfg.extractorToggleItem);
            if (ForgeRegistries.ITEMS.containsKey(rlItem)) {
                extractorToggle = ForgeRegistries.ITEMS.getValue(rlItem);
            } else {
                throw new LoaderException("Item with registry name " + rlItem + " not found.");
            }
        }
        checkCorrectMobs();
    }

    private static void checkCorrectMobs() {
        for (String str : cfg.blackListMobs)
            if (!ForgeRegistries.ENTITY_TYPES.containsKey(new ResourceLocation(str)))
                BioTechnik.log.error("Found wrong mobname -> " + str + " (BlackList)");
        for (String str : cfg.specificDrops.keySet())
            if (!ForgeRegistries.ENTITY_TYPES.containsKey(new ResourceLocation(str)))
                BioTechnik.log.error("Found wrong mobname -> " + str + " (SpecificDrops)");
    }

    private static void createDefault(File file) throws IOException {
        if (!file.exists())
            file.createNewFile();
        else {
            file.delete();
            file.createNewFile();
        }
        BTConfigGSON cfg = new BTConfigGSON();
        cfg.blackListMobs = BTConfigsAPI.blackListMobs;
        cfg.specificDrops = BTConfigsAPI.specificDrops;
        cfg.blackListMobs.add(new ResourceLocation("wither").toString());
        cfg.blackListMobs.add(new ResourceLocation("ender_dragon").toString());
        cfg.specificDrops.put(new ResourceLocation("skeleton").toString(), 100);
        cfg.specificDrops.put(new ResourceLocation("zombie").toString(), 100);
        cfg.specificDrops.put(new ResourceLocation("spider").toString(), 100);
        cfg.specificDrops.put(new ResourceLocation("creeper").toString(), 100);
        cfg.maxAmount = 1500;
        cfg.radius = 2;
        cfg.extractorToggleItem = ForgeRegistries.ITEMS.getKey(Items.NETHER_STAR).toString();
        FileWriter fw = new FileWriter(file);
        fw.write(gson.toJson(cfg));
        fw.close();
    }

    public static boolean isBlackList(Entity entity) {
        if (entity == null || entity.getType() == null)
            return true;
        ResourceLocation rl = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        if (rl == null)
            return true;
        return isBlackList(rl.toString());
    }

    public static boolean isBlackList(String name) {
        return cfg.blackListMobs.contains(name);
    }

    public static boolean hasSpecificDrop(Entity entity) {
        if (entity == null || entity.getType() == null)
            return false;
        return cfg.specificDrops.containsKey(ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString());
    }

    public static int getSpecificDrop(Entity entity) {
        return cfg.specificDrops.get(ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString());
    }

    public static int getMaxAmount() {
        return cfg.maxAmount;
    }

    public static int getRadius() {
        return cfg.radius;
    }

    public static Item getExtractorToggle() {
        return extractorToggle;
    }
}
