package wbe.lastHunters.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import wbe.lastHunters.LastHunters;

public class EntityTargetLivingEntityListeners implements Listener {

    private LastHunters plugin = LastHunters.getInstance();

    @EventHandler(priority = EventPriority.MONITOR)
    public void cancelSpecialMobTargeting(EntityTargetLivingEntityEvent event) {
        if(!(event.getTarget() instanceof Player)) {
            return;
        }

        NamespacedKey mobKey = new NamespacedKey(plugin, "specialMob");
        if(event.getEntity().getPersistentDataContainer().has(mobKey)) {
            event.setCancelled(true);
        }
    }
}
