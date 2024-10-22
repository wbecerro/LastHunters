package wbe.lastHunters.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.EntitiesUnloadEvent;
import wbe.lastHunters.LastHunters;
import wbe.lastHunters.config.locations.GolemSpot;

public class EntitiesUnloadListeners implements Listener {

    private LastHunters plugin = LastHunters.getInstance();

    @EventHandler(priority = EventPriority.NORMAL)
    public void removeGolemsOnUnload(EntitiesUnloadEvent event) {
        NamespacedKey golemKey = new NamespacedKey(plugin, "golem");
        for(Entity entity : event.getEntities()) {
            if(!(entity instanceof LivingEntity)) {
                continue;
            }

            if(entity.getPersistentDataContainer().has(golemKey)) {
                continue;
            }

            entity.remove();
            GolemSpot.spawnedGolems--;
        }
    }
}
