package wbe.lastHunters.listeners;

import net.minecraft.world.entity.Mob;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftMob;
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
import wbe.lastHunters.config.entities.Golem;
import wbe.lastHunters.config.locations.ChickenCannon;
import wbe.lastHunters.config.locations.GolemSpot;
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
        NamespacedKey cannonKey = new NamespacedKey(plugin, "cannon");
        String id = damaged.getPersistentDataContainer().get(cannonKey, PersistentDataType.STRING);
        ChickenCannon cannon = utilities.searchChickenCannon(id);
        ChickenCannon.spawnedChickens.put(cannon, ChickenCannon.spawnedChickens.get(cannon) - 1);
        damaged.remove();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void harmGolemOnDestroyerHit(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Projectile)) {
            return;
        }

        if(!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        LivingEntity damaged = (LivingEntity) event.getEntity();
        NamespacedKey golemKey = new NamespacedKey(plugin, "golem");
        if(!damaged.getPersistentDataContainer().has(golemKey)) {
            return;
        }

        Projectile projectile = (Projectile) event.getDamager();
        NamespacedKey projectileKey = new NamespacedKey(plugin, "destroyerProjectile");
        if(!projectile.getPersistentDataContainer().has(projectileKey)) {
            return;
        }

        NamespacedKey hpKey = new NamespacedKey(plugin, "golemHP");
        int hp = damaged.getPersistentDataContainer().get(hpKey, PersistentDataType.INTEGER) - 1;
        Golem golem = utilities.searchGolem(damaged.getPersistentDataContainer().get(golemKey, PersistentDataType.STRING));
        if(hp <= 0) {
            Player player = (Player) projectile.getShooter();
            int doubleChance = utilities.getPlayerDoubleChance(player);
            Random random = new Random();
            if(random.nextInt(100) + 1 < doubleChance) {
                player.sendMessage(LastHunters.messages.doubleDrop);
                utilities.giveReward(player, golem.getRandomRarity());
            }
            utilities.giveReward(player, golem.getRandomRarity());
            Mob mob = ((CraftMob) damaged).getHandle();
            GolemSpot.spawnedGolems--;
            damaged.remove();
        } else {
            damaged.setCustomName(golem.getName().replace("%hp%", LastHunters.config.golemHPIcon.repeat(hp)));
            damaged.getPersistentDataContainer().set(hpKey, PersistentDataType.INTEGER, hp);
        }
    }
}
