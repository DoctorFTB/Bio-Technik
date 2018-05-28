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
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

/**
 * Created by FTB_lag.
 */
public class BTConfigParser {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static BTConfigGSON cfg;

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
        checkCorrectMobs();
    }

    private static void checkCorrectMobs() {
        for (String str : cfg.blackListMobs)
            if (!ForgeRegistries.ENTITIES.containsKey(new ResourceLocation(str)))
                BioTechnik.log.error("Found wrong mobname -> " + str + " (BlackList)");
        for (String str : cfg.specificDrops.keySet())
            if (!ForgeRegistries.ENTITIES.containsKey(new ResourceLocation(str)))
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
        FileWriter fw = new FileWriter(file);
        fw.write(gson.toJson(cfg));
        fw.close();
    }

    public static boolean isBlackList(Entity entity) {
        return cfg.blackListMobs.contains(EntityRegistry.getEntry(entity.getClass()).getRegistryName().toString());
    }

    public static boolean hasSpecificDrop(Entity entity) {
        return cfg.specificDrops.containsKey(EntityRegistry.getEntry(entity.getClass()).getRegistryName().toString());
    }

    public static int getSpecificDrop(Entity entity) {
        return cfg.specificDrops.get(EntityRegistry.getEntry(entity.getClass()).getRegistryName().toString());
    }

    public static int getMaxAmount() {
        return cfg.maxAmount;
    }
}