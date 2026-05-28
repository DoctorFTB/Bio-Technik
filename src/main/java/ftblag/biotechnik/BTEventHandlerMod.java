package ftblag.biotechnik;

import ftblag.biotechnik.entity.RenderRFOrb;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = BioTechnik.MODID)
public class BTEventHandlerMod {

    @SubscribeEvent
    public static void doClientStuff(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(BTRegistry.ORB_TYPE.get(), RenderRFOrb::new);
    }
}
