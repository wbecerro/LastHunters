package wbe.lastHunters.listeners;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import wbe.lastHunters.LastHunters;
import wbe.lastHunters.config.locations.CatalystSpot;

public class BlockPlaceListeners implements Listener {

    private LastHunters plugin = LastHunters.getInstance();

    @EventHandler(priority = EventPriority.NORMAL)
    public void placeCatalystOnBoss(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        EquipmentSlot slot = event.getHand();
        ItemStack item = null;
        if(slot.equals(EquipmentSlot.HAND)) {
            item = player.getInventory().getItemInMainHand();
        } else if(slot.equals(EquipmentSlot.OFF_HAND)) {
            item = player.getInventory().getItemInOffHand();
        }

        ItemMeta meta = item.getItemMeta();
        if(meta == null) {
            return;
        }

        NamespacedKey catalystKey = new NamespacedKey(plugin, "catalyst");
        if(!meta.getPersistentDataContainer().has(catalystKey)) {
            return;
        }

        CatalystSpot.catalystPlaced += 1;
        
    }
}
