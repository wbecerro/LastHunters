package wbe.lastHunters.util;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;
import wbe.lastHunters.LastHunters;
import wbe.lastHunters.config.locations.ChickenCannon;

import java.util.Set;

public class Scheduler {

    private static Utilities utilities = new Utilities();

    public static void startSchedulers(FileConfiguration config, LastHunters plugin) {
        startPoolMobsScheduler(config, plugin);
        startChickenCannonScheduler(config, plugin);
        startGroundChickenRemoverScheduler(config, plugin);
        startChestFillScheduler(config, plugin);
    }

    private static void startPoolMobsScheduler(FileConfiguration config, LastHunters plugin) {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                for(String worldName : LastHunters.config.enabledWorlds) {
                    World world = Bukkit.getWorld(worldName);
                    Set<ProtectedRegion> regions = utilities.getValidRegions(world);
                    utilities.spawnPoolMobs(regions, world);
                }
            }
        }, 20L, LastHunters.config.poolMobsRespawnTime * 20L);
    }

    private static void startChickenCannonScheduler(FileConfiguration config, LastHunters plugin) {
        if(!LastHunters.config.enableChickens) {
            return;
        }

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                utilities.spawnChickensFromCannons();
            }
        }, 20L, LastHunters.config.chickenCannonFireTime * 20L);
    }

    private static void startGroundChickenRemoverScheduler(FileConfiguration config, LastHunters plugin) {
        NamespacedKey chickenKey = new NamespacedKey(plugin, "chicken");
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                for(String worldName : LastHunters.config.enabledWorlds) {
                    World world = Bukkit.getWorld(worldName);
                    for(LivingEntity entity : world.getLivingEntities()) {
                        if(entity.isOnGround() || entity.isDead() || entity.isInWater()) {
                            if(entity.getPersistentDataContainer().has(chickenKey)) {
                                NamespacedKey cannonKey = new NamespacedKey(plugin, "cannon");
                                String id = entity.getPersistentDataContainer().get(cannonKey, PersistentDataType.STRING);
                                ChickenCannon cannon = utilities.searchChickenCannon(id);
                                ChickenCannon.spawnedChickens.put(cannon, ChickenCannon.spawnedChickens.get(cannon) - 1);
                                entity.remove();
                            }
                        }
                    }
                }
            }
        }, 20L, 10L);
    }

    private static void startChestFillScheduler(FileConfiguration config, LastHunters plugin) {
        if(!LastHunters.config.enableChests) {
            return;
        }

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                utilities.fillChest();
            }
        }, 20L, 20L * LastHunters.config.refillChestsTime);
    }
}
