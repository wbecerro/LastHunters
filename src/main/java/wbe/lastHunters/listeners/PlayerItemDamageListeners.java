package wbe.lastHunters.listeners;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import wbe.lastHunters.LastHunters;

public class PlayerItemDamageListeners implements Listener {

    private LastHunters plugin = LastHunters.getInstance();

    private final int maxRodDamage = 64;

    @EventHandler(priority = EventPriority.NORMAL)
    public void sendTitleOnLowUses(PlayerItemDamageEvent event) {
        ItemStack item = event.getItem();
        ItemMeta meta = item.getItemMeta();
        if(meta == null) {
            return;
        }

        NamespacedKey rodKey = new NamespacedKey(plugin, "specialRod");
        if(!meta.getPersistentDataContainer().has(rodKey)) {
            return;
        }

        Damageable damageable = (Damageable) meta;
        if(!damageable.hasDamage()) {
            return;
        }

        if(maxRodDamage - damageable.getDamage() <= LastHunters.config.lowRodUses) {
            event.getPlayer().sendTitle(LastHunters.messages.lowRodTitle, LastHunters.messages.lowRodSubtitle, 10,
                    LastHunters.config.titleTime * 20, 20);
        }
    }
}
