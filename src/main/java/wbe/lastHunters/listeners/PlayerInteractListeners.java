package wbe.lastHunters.listeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import wbe.lastHunters.LastHunters;
import wbe.lastHunters.config.locations.DestroyerSpot;
import wbe.lastHunters.util.Utilities;

import java.util.List;

public class PlayerInteractListeners implements Listener {

    private LastHunters plugin = LastHunters.getInstance();

    private Utilities utilities = new Utilities();

    @EventHandler(priority = EventPriority.NORMAL)
    public void destroyerShootOnInteract(PlayerInteractEvent event) throws ClassNotFoundException {
        if(!event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            return;
        }

        ItemStack item = event.getItem();
        if(item == null || item.getType().equals(Material.AIR)) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if(meta == null) {
            return;
        }

        NamespacedKey destroyerKey = new NamespacedKey(plugin, "golemDestroyer");
        if(!meta.getPersistentDataContainer().has(destroyerKey)) {
            return;
        }

        Player player = event.getPlayer();
        DestroyerSpot spot = utilities.getValidDestroyerSpot(player);
        if(spot == null) {
            player.sendMessage(LastHunters.messages.cannotUseDestroyer);
            event.setCancelled(true);
            return;
        }

        NamespacedKey usesKey = new NamespacedKey(plugin, "destroyerUses");
        int uses = meta.getPersistentDataContainer().get(usesKey, PersistentDataType.INTEGER) - 1;
        if(uses > 0) {
            if(uses <= 10) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(LastHunters.messages.lowBowUses
                        .replace("%uses%", String.valueOf(uses))));
            }

            int line = utilities.findLine(item, LastHunters.config.golemDestroyerUsesLore.split("%uses%")[0]);
            if(line == -1) {
                line = utilities.findLine(item, LastHunters.config.golemDestroyerUsesLore.split("%uses%")[1]);
                if(line == -1) {
                    return;
                }
            }

            meta.getPersistentDataContainer().set(usesKey, PersistentDataType.INTEGER, uses);
            List<String> lore = meta.getLore();
            lore.set(line, LastHunters.config.usesLore.replace("%uses%", String.valueOf(uses)));
            meta.setLore(lore);
            item.setItemMeta(meta);
        } else {
            player.sendMessage(LastHunters.messages.destroyerBroken);
            item.setAmount(0);
        }

        NamespacedKey projectileKey = new NamespacedKey(plugin, "destroyerProjectile");
        Projectile projectile = player.launchProjectile(Snowball.class);
        projectile.setShooter(player);
        projectile.getPersistentDataContainer().set(projectileKey, PersistentDataType.BOOLEAN, true);
        event.setCancelled(true);
    }
}
