package ftblag.biotechnik;

import ftblag.biotechnik.block.BlockRFExtractor;
import ftblag.biotechnik.block.TileEntityRFExtractor;
import ftblag.biotechnik.entity.EntityRFOrb;
import ftblag.biotechnik.item.ItemRFCollector;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BTRegistry {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BioTechnik.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BioTechnik.MODID);
    private static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, BioTechnik.MODID);
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, BioTechnik.MODID);
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BioTechnik.MODID);

    public static RegistryObject<BlockRFExtractor> EXTRACTOR = BLOCKS.register("rfextractor", () -> new BlockRFExtractor(Block.Properties.of().strength(1f, 10f).sound(SoundType.METAL)));
    public static RegistryObject<BlockItem> EXTRACTOR_ITEM = ITEMS.register("rfextractor", () -> new BlockItem(EXTRACTOR.get(), new Item.Properties()));
    public static RegistryObject<ItemRFCollector> COLLECTOR = ITEMS.register("rfcollector", () -> new ItemRFCollector(new Item.Properties().stacksTo(1)));
    public static RegistryObject<BlockEntityType<TileEntityRFExtractor>> EXTRACTOR_TYPE = TILE_ENTITIES.register("extractor_tile", () -> BlockEntityType.Builder.of(TileEntityRFExtractor::new, EXTRACTOR.get()).build(null));
    public static RegistryObject<EntityType<EntityRFOrb>> ORB_TYPE = ENTITIES.register("rforb", () -> EntityType.Builder.<EntityRFOrb>of(EntityRFOrb::new, MobCategory.MISC).setCustomClientFactory(EntityRFOrb::new).sized(0.125F, 0.125F).build(BioTechnik.MODID + ":rforb"));

    public static RegistryObject<CreativeModeTab> TAB = TABS.register(
            BioTechnik.MODID,
            () -> CreativeModeTab.builder()
                .icon(() -> new ItemStack(BTRegistry.EXTRACTOR.get()))
                .title(Component.translatable("itemGroup.biotechnik"))
                .displayItems((supplier, output) ->
                        BTRegistry.ITEMS.getEntries()
                                .stream()
                                .map(RegistryObject::get)
                                .forEach(output::accept))
                .build()
    );

    public static void register() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILE_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        TABS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
