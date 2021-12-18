package ftblag.biotechnik;

import ftblag.biotechnik.block.BlockRFExtractor;
import ftblag.biotechnik.block.TileEntityRFExtractor;
import ftblag.biotechnik.config.BTConfigParser;
import ftblag.biotechnik.entity.EntityRFOrb;
import ftblag.biotechnik.item.ItemRFCollector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(BioTechnik.MODID)
public class BioTechnik {

    public static final String MODID = "biotechnik";
    public static final Logger log = LogManager.getLogger();

    public static BlockRFExtractor extractor = new BlockRFExtractor(Block.Properties.of(Material.STONE).strength(1f, 10f).sound(SoundType.METAL));

    public static CreativeModeTab group = new CreativeModeTab(MODID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(BioTechnik.extractor);
        }
    };

    public static BlockEntityType<TileEntityRFExtractor> extractorType = BlockEntityType.Builder.of(TileEntityRFExtractor::new, extractor).build(null);
    public static ItemRFCollector collector = new ItemRFCollector(new Item.Properties().stacksTo(1).tab(BioTechnik.group));
    public static EntityType<EntityRFOrb> orbType = EntityType.Builder.<EntityRFOrb>of(EntityRFOrb::new, MobCategory.MISC).setCustomClientFactory(EntityRFOrb::new).sized(0.125F, 0.125F).build(MODID + ":rforb");

    public BioTechnik() {
        BTConfigParser.parseFile(new File(FMLPaths.CONFIGDIR.get().toFile(), MODID + ".json"));
    }
}
