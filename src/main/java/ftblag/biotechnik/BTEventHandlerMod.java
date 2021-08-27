package ftblag.biotechnik;

import ftblag.biotechnik.entity.RenderRFOrb;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
        event.getRegistry().register(new BlockItem(BioTechnik.extractor, new Item.Properties().tab(BioTechnik.group)).setRegistryName(BioTechnik.extractor.getRegistryName()));
        event.getRegistry().register(BioTechnik.collector);
    }

    @SubscribeEvent
    public static void onTileRegistry(RegistryEvent.Register<BlockEntityType<?>> event) {
        event.getRegistry().register(BioTechnik.extractorType.setRegistryName(BioTechnik.MODID, "extractor_tile"));
    }

    @SubscribeEvent
    public static void onEntityRegistry(RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().register(BioTechnik.orbType.setRegistryName(BioTechnik.MODID, "rforb"));
    }

    @SubscribeEvent
    public static void doClientStuff(FMLClientSetupEvent event) {
        EntityRenderers.register(BioTechnik.orbType, RenderRFOrb::new);
    }
}
