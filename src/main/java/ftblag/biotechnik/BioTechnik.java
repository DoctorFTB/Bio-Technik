package ftblag.biotechnik;

import java.io.File;

import org.apache.logging.log4j.Logger;

import ftblag.biotechnik.block.BlockRFExtractor;
import ftblag.biotechnik.config.BTConfigParser;
import ftblag.biotechnik.entity.EntityRFOrb;
import ftblag.biotechnik.entity.RenderRFOrb;
import ftblag.biotechnik.item.ItemRFCollector;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by FTB_lag.
 */
@Mod(modid = BioTechnik.MODID, name = BioTechnik.MODNAME, version = BioTechnik.VERSION)
public class BioTechnik {

    public static final String MODID = "biotechnik", MODNAME = "Bio Technik", VERSION = "@VERSION@";
    public static Logger log;
    public static ItemRFCollector collector;
    public static BlockRFExtractor extractor;

    @Mod.Instance
    public BioTechnik instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        log = e.getModLog();
        collector = new ItemRFCollector();
        extractor = new BlockRFExtractor();
        BTConfigParser.parseFile(new File(e.getModConfigurationDirectory(), MODID + ".json"));
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "RFOrb"), EntityRFOrb.class, "RFOrb", 0, instance,
                120, 20, true);
        if (e.getSide().isClient())
            clientInit();
    }

    @SideOnly(Side.CLIENT)
    private void clientInit() {
        RenderingRegistry.registerEntityRenderingHandler(EntityRFOrb.class, new RenderRFOrb());
    }
}