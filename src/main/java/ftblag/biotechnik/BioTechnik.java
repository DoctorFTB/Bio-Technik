package ftblag.biotechnik;

import ftblag.biotechnik.config.BTConfigParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.RegistryObject;
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

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerTabs);
    }

    private void registerTabs(CreativeModeTabEvent.Register event) {
        event.registerCreativeModeTab(new ResourceLocation(MODID, MODID), builder -> builder
                .icon(() -> new ItemStack(BTRegistry.EXTRACTOR.get()))
                .title(Component.translatable("itemGroup.biotechnik"))
                .displayItems((featureFlags, output, hasOp) ->
                        BTRegistry.ITEMS.getEntries()
                                .stream()
                                .map(RegistryObject::get)
                                .forEach(output::accept)
                )
        );
    }
}
