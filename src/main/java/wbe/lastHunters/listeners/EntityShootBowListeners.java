package wbe.lastHunters.listeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import wbe.lastHunters.LastHunters;
import wbe.lastHunters.config.locations.BowSpot;
import wbe.lastHunters.util.Utilities;

import java.util.List;

public class EntityShootBowListeners implements Listener {

    private LastHunters plugin = LastHunters.getInstance();

    private Utilities utilities = new Utilities();

    @EventHandler(priority = EventPriority.NORMAL)
    public void specialBowShoot(EntityShootBowEvent event) {
        if(!(event.getEntity() instanceof Player)) {
            return;
        }

        ItemStack bow = event.getBow();
        ItemMeta meta = bow.getItemMeta();
        if(meta == null) {
            return;
        }

        NamespacedKey bowKey = new NamespacedKey(plugin, "specialBow");
        if(!meta.getPersistentDataContainer().has(bowKey)) {
            return;
        }

        Player player = (Player) event.getEntity();
        BowSpot spot = utilities.getValidSpot(player);
        if(spot == null) {
            player.sendMessage(LastHunters.messages.cannotUseBow);
            event.setCancelled(true);
            return;
        }

        NamespacedKey usesKey = new NamespacedKey(plugin, "bowUses");
        int uses = meta.getPersistentDataContainer().get(usesKey, PersistentDataType.INTEGER) - 1;
        if(uses > 0) {
            if(uses <= 10) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(LastHunters.messages.lowBowUses
                        .replace("%uses%", String.valueOf(uses))));
            }

            int line = utilities.findLine(bow, LastHunters.config.usesLore.split("%uses%")[0]);
            if(line == -1) {
                line = utilities.findLine(bow, LastHunters.config.usesLore.split("%uses%")[1]);
                if(line == -1) {
                    return;
                }
            }

            meta.getPersistentDataContainer().set(usesKey, PersistentDataType.INTEGER, uses);
            List<String> lore = meta.getLore();
            lore.set(line, LastHunters.config.usesLore.replace("%uses%", String.valueOf(uses)));
            meta.setLore(lore);
            bow.setItemMeta(meta);
        } else {
            player.sendMessage(LastHunters.messages.bowBroken);
            bow.setAmount(0);
        }

        NamespacedKey projectileKey = new NamespacedKey(plugin, "specialBowProjectile");
        Projectile projectile = player.launchProjectile(WitherSkull.class);
        projectile.setVelocity(event.getProjectile().getVelocity());
        projectile.setShooter(player);
        projectile.getPersistentDataContainer().set(projectileKey, PersistentDataType.BOOLEAN, true);
        event.setProjectile(null);
    }
}
