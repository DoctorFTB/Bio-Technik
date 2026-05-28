package ftblag.biotechnik;

import ftblag.biotechnik.block.BlockRFExtractor;
import ftblag.biotechnik.block.TileEntityRFExtractor;
import ftblag.biotechnik.entity.EntityRFOrb;
import ftblag.biotechnik.item.ItemRFCollector;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BTRegistry {
    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, BioTechnik.MODID);
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, BioTechnik.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, BioTechnik.MODID);
    private static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, BioTechnik.MODID);
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, BioTechnik.MODID);
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BioTechnik.MODID);

    public static DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> RF = DATA_COMPONENTS.register("rf", () -> DataComponentType.<Integer>builder().persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT).cacheEncoding().build());
    public static DeferredHolder<Block, BlockRFExtractor> EXTRACTOR = BLOCKS.register("rfextractor", () -> new BlockRFExtractor(Block.Properties.of().strength(1f, 10f).sound(SoundType.METAL).setId(getKeyForBlock("rfextractor"))));
    public static DeferredHolder<Item, BlockItem> EXTRACTOR_ITEM = ITEMS.register("rfextractor", () -> new BlockItem(EXTRACTOR.get(), new Item.Properties().setId(getKeyForItem("rfextractor"))));
    public static DeferredHolder<Item, ItemRFCollector> COLLECTOR = ITEMS.register("rfcollector", () -> new ItemRFCollector(new Item.Properties().stacksTo(1).setId(getKeyForItem("rfcollector"))));
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEntityRFExtractor>> EXTRACTOR_TYPE = TILE_ENTITIES.register("extractor_tile", () -> new BlockEntityType<>(TileEntityRFExtractor::new, EXTRACTOR.get()));
    public static DeferredHolder<EntityType<?>, EntityType<EntityRFOrb>> ORB_TYPE = ENTITIES.register("rforb", () -> EntityType.Builder.<EntityRFOrb>of(EntityRFOrb::new, MobCategory.MISC).sized(0.125F, 0.125F).build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(BioTechnik.MODID, "rforb"))));

    public static DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = TABS.register(
            BioTechnik.MODID,
            () -> CreativeModeTab.builder()
                .icon(() -> new ItemStack(BTRegistry.EXTRACTOR.get()))
                .title(Component.translatable("itemGroup.biotechnik"))
                .displayItems((supplier, output) ->
                        BTRegistry.ITEMS.getEntries()
                                .stream()
                                .map(DeferredHolder::get)
                                .forEach(output::accept))
                .build()
    );

    public static void register(IEventBus modEventBus) {
        DATA_COMPONENTS.register(modEventBus);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        TILE_ENTITIES.register(modEventBus);
        ENTITIES.register(modEventBus);
        TABS.register(modEventBus);
    }

    public static ResourceKey<Item> getKeyForItem(String path) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(BioTechnik.MODID, path));
    }

    public static ResourceKey<Block> getKeyForBlock(String path) {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(BioTechnik.MODID, path));
    }
}
