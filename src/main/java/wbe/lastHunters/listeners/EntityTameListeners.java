package wbe.lastHunters.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;
import wbe.lastHunters.LastHunters;

public class EntityTameListeners implements Listener {

    private LastHunters plugin = LastHunters.getInstance();

    @EventHandler(priority = EventPriority.NORMAL)
    public void cancelTameOnSpecialMob(EntityTameEvent event) {
        NamespacedKey mobKey = new NamespacedKey(plugin, "specialMob");
        if(event.getEntity().getPersistentDataContainer().has(mobKey)) {
            event.setCancelled(true);
        }
    }
}
