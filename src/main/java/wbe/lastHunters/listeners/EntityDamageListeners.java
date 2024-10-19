package wbe.lastHunters.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataType;
import wbe.lastHunters.LastHunters;
import wbe.lastHunters.config.entities.Chicken;
import wbe.lastHunters.util.Utilities;

import java.util.Random;

public class EntityDamageListeners implements Listener {

    private LastHunters plugin = LastHunters.getInstance();

    private Utilities utilities = new Utilities();

    @EventHandler(priority = EventPriority.NORMAL)
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

    @EventHandler(priority = EventPriority.NORMAL)
    public void cancelDamageOnPlayerBySpecialMob(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player)) {
            return;
        }

        NamespacedKey mobKey = new NamespacedKey(plugin, "specialMob");
        if(event.getDamager().getPersistentDataContainer().has(mobKey)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void destroyChickenOnImpact(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Projectile)) {
            return;
        }

        if(!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        LivingEntity damaged = (LivingEntity) event.getEntity();
        NamespacedKey chickenKey = new NamespacedKey(plugin, "chicken");
        if(!damaged.getPersistentDataContainer().has(chickenKey)) {
            return;
        }

        Projectile projectile = (Projectile) event.getDamager();
        NamespacedKey projectileKey = new NamespacedKey(plugin, "specialBowProjectile");
        if(!projectile.getPersistentDataContainer().has(projectileKey)) {
            return;
        }

        Chicken chicken = utilities.searchChicken(damaged.getPersistentDataContainer().get(chickenKey, PersistentDataType.STRING));
        Player player = (Player) projectile.getShooter();
        int doubleChance = utilities.getPlayerDoubleChance(player);
        Random random = new Random();
        if(random.nextInt(100) + 1 < doubleChance) {
            player.sendMessage(LastHunters.messages.doubleDrop);
            utilities.giveReward(player, chicken.getRandomRarity());
        }
        utilities.giveReward(player, chicken.getRandomRarity());
        damaged.remove();
    }

}
