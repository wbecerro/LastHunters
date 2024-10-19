package wbe.lastHunters.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import wbe.lastHunters.LastHunters;

public class EntityDamageListeners implements Listener {

    private LastHunters plugin = LastHunters.getInstance();

    @EventHandler(priority = EventPriority.MONITOR)
    public void cancelDamageOnSpecialMob(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        LivingEntity mob = (LivingEntity) event.getEntity();
        NamespacedKey mobKey = new NamespacedKey(plugin, "specialMob");
        if(!mob.getPersistentDataContainer().has(mobKey)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void cancelDamageOnPlayerBySpecialMob(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player)) {
            return;
        }

        NamespacedKey mobKey = new NamespacedKey(plugin, "specialMob");
        if(event.getDamager().getPersistentDataContainer().has(mobKey)) {
            event.setCancelled(true);
        }
    }
}
