package ftblag.biotechnik;

import ftblag.biotechnik.config.BTConfigParser;
import ftblag.biotechnik.entity.EntityRFOrb;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = BioTechnik.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BTEventHandlerForge {

    @SubscribeEvent
    public static void drops(LivingDropsEvent e) {
        Entity entity = e.getEntity();
        World world = entity.world;
        if (!world.isRemote) {
            if (!BTConfigParser.isBlackList(entity)) {
                System.out.println(ForgeRegistries.ENTITIES.getKey(entity.getType()));
                int amount = BTConfigParser.hasSpecificDrop(entity) ? BTConfigParser.getSpecificDrop(entity) : world.rand.nextInt(BTConfigParser.getMaxAmount());
                EntityRFOrb orb = new EntityRFOrb(world, entity.posX, entity.posY, entity.posZ, amount);
                world.addEntity(orb);
            }
        }
    }
}
