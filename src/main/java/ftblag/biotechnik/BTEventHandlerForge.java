package ftblag.biotechnik;

import ftblag.biotechnik.config.BTConfigParser;
import ftblag.biotechnik.entity.EntityRFOrb;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BioTechnik.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BTEventHandlerForge {

    @SubscribeEvent
    public static void drops(LivingDropsEvent e) {
        Entity entity = e.getEntity();
        World level = entity.level;
        if (!level.isClientSide) {
            if (!BTConfigParser.isBlackList(entity)) {
                int amount = BTConfigParser.hasSpecificDrop(entity) ? BTConfigParser.getSpecificDrop(entity) : level.random.nextInt(BTConfigParser.getMaxAmount());
                level.addFreshEntity(new EntityRFOrb(level, entity.getX(), entity.getY(), entity.getZ(), amount));
            }
        }
    }
}
