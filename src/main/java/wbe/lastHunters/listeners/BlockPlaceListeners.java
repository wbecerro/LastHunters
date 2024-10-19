package wbe.lastHunters.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import wbe.lastHunters.LastHunters;
import wbe.lastHunters.config.locations.CatalystSpot;
import wbe.lastHunters.util.Utilities;

public class BlockPlaceListeners implements Listener {

    private LastHunters plugin = LastHunters.getInstance();

    private Utilities utilities = new Utilities();

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
        String id = meta.getPersistentDataContainer().get(catalystKey, PersistentDataType.STRING);

        CatalystSpot catalystSpot = utilities.searchCatalystSpot(id);
        if(!catalystSpot.isHeadPlaceable(event.getBlockPlaced().getLocation(), id)) {
            player.sendMessage(LastHunters.messages.wrongHeadLocation);
            event.setCancelled(true);
            return;
        }

        utilities.placeCatalystInteraction(utilities.searchCatalyst(id), player, event.getBlockPlaced().getLocation());
    }
}
