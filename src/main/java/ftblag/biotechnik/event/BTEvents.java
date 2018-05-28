package ftblag.biotechnik.event;

import ftblag.biotechnik.BioTechnik;
import ftblag.biotechnik.config.BTConfigParser;
import ftblag.biotechnik.entity.EntityRFOrb;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by FTB_lag.
 */
@Mod.EventBusSubscriber(modid = BioTechnik.MODID)
public class BTEvents {

    @SubscribeEvent
    public static void drops(LivingDropsEvent e) {
        Entity entity = e.getEntity();
        World world = entity.world;
        if (!world.isRemote)
            if (!BTConfigParser.isBlackList(entity)) {
                int amount = BTConfigParser.hasSpecificDrop(entity) ? BTConfigParser.getSpecificDrop(entity)
                        : world.rand.nextInt(BTConfigParser.getMaxAmount());
                EntityRFOrb orb = new EntityRFOrb(world, entity.posX, entity.posY, entity.posZ, amount);
                world.spawnEntity(orb);
            }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent e) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BioTechnik.extractor), 0,
                new ModelResourceLocation(BioTechnik.extractor.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(BioTechnik.collector, 0,
                new ModelResourceLocation(BioTechnik.collector.getRegistryName(), "inventory"));
    }

    @SubscribeEvent
    public static void registerRecipe(Register<IRecipe> e) {
        GameRegistry.addShapedRecipe(new ResourceLocation(BioTechnik.MODID, "collector"),
                new ResourceLocation(BioTechnik.MODID, "collector"), new ItemStack(BioTechnik.collector), "ORO", "O O",
                " O ", 'O', Blocks.OBSIDIAN, 'R', Blocks.REDSTONE_BLOCK);
        GameRegistry.addShapedRecipe(new ResourceLocation(BioTechnik.MODID, "extractor"),
                new ResourceLocation(BioTechnik.MODID, "extractor"), new ItemStack(BioTechnik.extractor), "OOO", "OBO",
                "OOO", 'O', Blocks.OBSIDIAN, 'B', Blocks.STONEBRICK);
    }
}