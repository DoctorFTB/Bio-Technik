package ftblag.biotechnik;

import ftblag.biotechnik.config.BTConfigParser;
import ftblag.biotechnik.entity.EntityRFOrb;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = BioTechnik.MODID, bus = EventBusSubscriber.Bus.GAME)
public class BTEventHandlerForge {

    @SubscribeEvent
    public static void drops(LivingDropsEvent e) {
        Entity entity = e.getEntity();
        Level level = entity.level();
        if (!level.isClientSide) {
            if (!BTConfigParser.isBlackList(entity)) {
                int amount = BTConfigParser.hasSpecificDrop(entity) ? BTConfigParser.getSpecificDrop(entity) : level.random.nextInt(BTConfigParser.getMaxAmount());
                level.addFreshEntity(new EntityRFOrb(level, entity.getX(), entity.getY(), entity.getZ(), amount));
            }
        }
    }
}
