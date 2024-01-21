package ftblag.biotechnik;

import ftblag.biotechnik.config.BTConfigParser;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(BioTechnik.MODID)
public class BioTechnik {

    public static final String MODID = "biotechnik";
    public static final Logger log = LogManager.getLogger();

    public BioTechnik() {
        BTRegistry.register();
        BTConfigParser.parseFile(new File(FMLPaths.CONFIGDIR.get().toFile(), MODID + ".json"));
    }
}
