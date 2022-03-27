package ftblag.biotechnik;

import ftblag.biotechnik.block.BlockRFExtractor;
import ftblag.biotechnik.block.TileEntityRFExtractor;
import ftblag.biotechnik.entity.EntityRFOrb;
import ftblag.biotechnik.item.ItemRFCollector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BTRegistry {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BioTechnik.MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BioTechnik.MODID);
    private static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, BioTechnik.MODID);
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, BioTechnik.MODID);

    public static RegistryObject<BlockRFExtractor> EXTRACTOR = BLOCKS.register("rfextractor", () -> new BlockRFExtractor(Block.Properties.of(Material.STONE).strength(1f, 10f).sound(SoundType.METAL)));
    public static RegistryObject<BlockItem> EXTRACTOR_ITEM = ITEMS.register("rfextractor", () -> new BlockItem(EXTRACTOR.get(), new Item.Properties().tab(BioTechnik.group)));
    public static RegistryObject<ItemRFCollector> COLLECTOR = ITEMS.register("rfcollector", () -> new ItemRFCollector(new Item.Properties().stacksTo(1).tab(BioTechnik.group)));
    public static RegistryObject<BlockEntityType<TileEntityRFExtractor>> EXTRACTOR_TYPE = TILE_ENTITIES.register("extractor_tile", () -> BlockEntityType.Builder.of(TileEntityRFExtractor::new, EXTRACTOR.get()).build(null));
    public static RegistryObject<EntityType<EntityRFOrb>> ORB_TYPE = ENTITIES.register("rforb", () -> EntityType.Builder.<EntityRFOrb>of(EntityRFOrb::new, MobCategory.MISC).setCustomClientFactory(EntityRFOrb::new).sized(0.125F, 0.125F).build(BioTechnik.MODID + ":rforb"));

    public static void register() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILE_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
