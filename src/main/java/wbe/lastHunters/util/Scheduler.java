package wbe.lastHunters.util;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import wbe.lastHunters.LastHunters;

import java.util.Set;

public class Scheduler {

    public static void startSchedulers(FileConfiguration config, LastHunters plugin) {
        startPoolMobsScheduler(config, plugin);
    }

    private static void startPoolMobsScheduler(FileConfiguration config, LastHunters plugin) {
        Utilities utilities = new Utilities();
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                for(String worldName : LastHunters.config.enabledWorlds) {
                    World world = Bukkit.getWorld(worldName);
                    Set<ProtectedRegion> regions = utilities.getValidRegions(world);
                    utilities.spawnPoolMobs(regions, world);
                }
            }
        }, 20L, LastHunters.config.poolMobsRespawnTime * 20);
    }
}
