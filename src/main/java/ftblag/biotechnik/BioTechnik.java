package ftblag.biotechnik;

import ftblag.biotechnik.block.BlockRFExtractor;
import ftblag.biotechnik.block.TileEntityRFExtractor;
import ftblag.biotechnik.config.BTConfigParser;
import ftblag.biotechnik.entity.EntityRFOrb;
import ftblag.biotechnik.item.ItemRFCollector;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(BioTechnik.MODID)
public class BioTechnik {

    public static final String MODID = "biotechnik";
    public static final Logger log = LogManager.getLogger();

    public static BlockRFExtractor extractor = new BlockRFExtractor(Block.Properties.create(Material.ROCK).hardnessAndResistance(1f, 10f).sound(SoundType.METAL));

    public static ItemGroup group = new ItemGroup(MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(BioTechnik.extractor);
        }
    };

    public static TileEntityType<TileEntityRFExtractor> extractorType = TileEntityType.Builder.create(TileEntityRFExtractor::new, extractor).build(null);
    public static ItemRFCollector collector = new ItemRFCollector(new Item.Properties().maxStackSize(1).group(BioTechnik.group));
    public static EntityType<EntityRFOrb> orbType = EntityType.Builder.<EntityRFOrb>create(EntityRFOrb::new, EntityClassification.MISC).setCustomClientFactory(EntityRFOrb::new).size(0.125F, 0.125F).build(MODID + ":rforb");

    public BioTechnik() {
//        MinecraftForge.EVENT_BUS.addListener(BTEventHandler::drops);
        BTConfigParser.parseFile(new File(FMLPaths.CONFIGDIR.get().toFile(), MODID + ".json"));
    }
}
