package ftblag.biotechnik;

import ftblag.biotechnik.entity.RenderRFOrb;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = BioTechnik.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BTEventHandlerMod {

    @SubscribeEvent
    public static void doClientStuff(FMLClientSetupEvent event) {
        EntityRenderers.register(BTRegistry.ORB_TYPE.get(), RenderRFOrb::new);
    }
}
