package ftblag.biotechnik;

import ftblag.biotechnik.entity.EntityRFOrb;
import ftblag.biotechnik.entity.RenderRFOrb;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = BioTechnik.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BTEventHandlerMod {

    @SubscribeEvent
    public static void onBlockRegistry(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(BioTechnik.extractor);
    }

    @SubscribeEvent
    public static void onItemRegistry(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new BlockItem(BioTechnik.extractor, new Item.Properties().group(BioTechnik.group).addToolType(ToolType.PICKAXE, 2)).setRegistryName(BioTechnik.extractor.getRegistryName()));
        event.getRegistry().register(BioTechnik.collector);
    }

    @SubscribeEvent
    public static void onTileRegistry(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().register(BioTechnik.extractorType.setRegistryName(BioTechnik.MODID, "extractor_tile"));
    }

    @SubscribeEvent
    public static void onEntityRegistry(RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().register(BioTechnik.orbType.setRegistryName(BioTechnik.MODID, "rforb"));
    }

    @SubscribeEvent
    public static void doClientStuff(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityRFOrb.class, RenderRFOrb::new);
    }
}
