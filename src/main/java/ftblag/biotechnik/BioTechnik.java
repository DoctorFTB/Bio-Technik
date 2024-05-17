package ftblag.biotechnik;

import ftblag.biotechnik.config.BTConfigParser;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.bus.api.IEventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(BioTechnik.MODID)
public class BioTechnik {

    public static final String MODID = "biotechnik";
    public static final Logger log = LogManager.getLogger();

    public BioTechnik(IEventBus modEventBus) {
        BTRegistry.register(modEventBus);
        BTConfigParser.parseFile(new File(FMLPaths.CONFIGDIR.get().toFile(), MODID + ".json"));
    }
}
