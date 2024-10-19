package wbe.lastHunters.util;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import wbe.lastHunters.LastHunters;
import wbe.lastHunters.config.entities.PoolMob;
import wbe.lastHunters.hooks.WorldGuardManager;
import wbe.lastHunters.rarities.Rarity;
import wbe.lastHunters.rarities.Reward;

import java.util.*;

public class Utilities {

    private LastHunters plugin = LastHunters.getInstance();

    public int getPlayerRodChance(Player player) {
        int chance = 0;

        PlayerInventory inventory = player.getInventory();
        ItemStack mainHand = inventory.getItemInMainHand();
        ItemStack offHand = inventory.getItemInOffHand();
        ItemStack[] armor = inventory.getArmorContents();

        if(!mainHand.getType().equals(Material.AIR)) {
            chance += getItemRodChance(mainHand);
        }

        if(!offHand.getType().equals(Material.AIR)) {
            chance += getItemRodChance(offHand);
        }

        for(ItemStack item : armor) {
            if(item == null) {
                continue;
            }
            chance += getItemRodChance(item);
        }

        return chance;
    }

    private int getItemRodChance(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if(meta == null) {
            return 0;
        }

        NamespacedKey baseRodKey = new NamespacedKey(plugin, "rodChance");
        if(meta.getPersistentDataContainer().has(baseRodKey)) {
            return meta.getPersistentDataContainer().get(baseRodKey, PersistentDataType.INTEGER);
        }

        return 0;
    }

    public Set<ProtectedRegion> getValidRegions(World world) {
        Set<ProtectedRegion> validRegions = new HashSet<>();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(world));
        Map<String, ProtectedRegion> regions = regionManager.getRegions();
        if(regions == null) {
            return validRegions;
        }

        regions.forEach((regionName, region) -> {
            region.getFlags().forEach((flag, value) -> {
                if(flag.getName().equalsIgnoreCase(WorldGuardManager.poolMobsFlag.getName())) {
                    if(value.toString().equalsIgnoreCase("ALLOW")) {
                        if(PoolMob.spawnedMobs.get(region.getId()) == null) {
                            PoolMob.spawnedMobs.put(region.getId(), 0);
                        }
                        validRegions.add(region);
                    }
                }
            });
        });

        return validRegions;
    }

    public void spawnPoolMobs(Set<ProtectedRegion> regions, World world) {
        Random random = new Random();
        for(ProtectedRegion region : regions) {
            int minX = region.getMinimumPoint().x();
            int minZ = region.getMinimumPoint().z();
            int maxX = region.getMaximumPoint().x();
            int maxZ = region.getMaximumPoint().z();
            int iterations = LastHunters.config.maxRegionMobs;
            if(PoolMob.spawnedMobs.get(region.getId()) >= LastHunters.config.maxRegionMobs) {
                continue;
            } else {
                iterations = iterations - PoolMob.spawnedMobs.get(region.getId());
            }

            for(int i=0;i<iterations;i++) {
                int x = random.nextInt(maxX - minX) + minX;
                int z = random.nextInt(maxZ - minZ) + minZ;
                int y = 0;
                if(region.getMinimumPoint().y() < region.getMaximumPoint().y()) {
                    y = region.getMinimumPoint().y() + 1;
                } else {
                    y = region.getMaximumPoint().y() + 1;
                }

                if(x == maxX) {
                    if (x < 0)
                        x--;
                } else if(x == minX && x < 0) {
                    x++;
                }
                if(z == maxZ) {
                    if (z < 0)
                        z--;
                } else if(z == minZ && z < 0) {
                    z++;
                }

                Block block = world.getBlockAt(x, y, z);
                while(!block.getType().equals(Material.AIR)) {
                    y++;
                    block = world.getBlockAt(x, y, z);
                }

                PoolMob.spawnedMobs.put(region.getId(), PoolMob.spawnedMobs.get(region.getId()) + 1);
                PoolMob poolMob = getRandomPoolMob();
                LivingEntity mob = (LivingEntity) world.spawnEntity(block.getLocation(), poolMob.getType());
                mob.setCustomName(poolMob.getName());
                NamespacedKey mobKey = new NamespacedKey(plugin, "specialMob");
                NamespacedKey poolKey = new NamespacedKey(plugin, "poolMob");
                NamespacedKey regionKey = new NamespacedKey(plugin, "mobRegion");
                mob.getPersistentDataContainer().set(mobKey, PersistentDataType.BOOLEAN, true);
                mob.getPersistentDataContainer().set(poolKey, PersistentDataType.STRING, poolMob.getId());
                mob.getPersistentDataContainer().set(regionKey, PersistentDataType.STRING, region.getId());
                mob.setRemoveWhenFarAway(false);
                mob.setCanPickupItems(false);
                mob.getEquipment().clear();
                mob.getPassengers().clear();
            }
        }
    }

    public PoolMob getRandomPoolMob() {
        Random random = new Random();
        int randomNumber = random.nextInt(LastHunters.config.maxPoolWeight);
        int weight = 0;
        Set<PoolMob> poolMobs = LastHunters.config.poolMobs;
        PoolMob mob = null;

        for(PoolMob poolMob : poolMobs) {
            mob = poolMob;
            weight += poolMob.getWeight();
            if(randomNumber < weight) {
                return poolMob;
            }
        }

        return mob;
    }

    public PoolMob searchPoolMob(String id) {
        for(PoolMob mob : LastHunters.config.poolMobs) {
            if(mob.getId().equalsIgnoreCase(id)) {
                return mob;
            }
        }

        return null;
    }

    public void giveReward(Player player, Rarity rarity) {
        if(rarity == null) {
            player.sendMessage(LastHunters.messages.noReward);
            return;
        }
        Reward reward = rarity.getRandomReward();
        String command = reward.getCommand().replace("%player%", player.getName());
        if(!rarity.getBroadcast().isEmpty()) {
            Bukkit.getServer().broadcastMessage(rarity.getBroadcast().replace("%player%", player.getName()));
        }

        if(rarity.getFireworks() != -1) {
            for(int i=0;i<rarity.getFireworks();i++) {
                Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK_ROCKET);
                firework.setFireworkMeta(getRandomFirework(firework));
            }
        }

        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
        String message = rarity.getPrefix() + reward.getMessage();
        player.sendMessage(message);
    }

    public String getRandomFailMessage() {
        Random random = new Random();
        int size = LastHunters.messages.failedCatch.size();
        return LastHunters.messages.failedCatch.get(random.nextInt(size)).replace("&", "§");
    }

    public void removeAllSpecialMobs() {
        for(String worldName : LastHunters.config.enabledWorlds) {
            World world = Bukkit.getWorld(worldName);
            for(LivingEntity entity : world.getLivingEntities()) {
                NamespacedKey mobKey = new NamespacedKey(plugin, "specialMob");
                if(entity.getPersistentDataContainer().has(mobKey)) {
                    entity.remove();
                    PoolMob.spawnedMobs.clear();
                }
            }
        }
    }

    private FireworkMeta getRandomFirework(Firework firework) {
        Random random = new Random();
        FireworkMeta meta = firework.getFireworkMeta();
        meta.setPower(random.nextInt(3) + 1);
        meta.addEffect(FireworkEffect.builder()
                .with(getRandomFireworkType())
                .flicker(random.nextBoolean())
                .trail(random.nextBoolean())
                .withColor(Color.fromRGB(random.nextInt(255), random.nextInt(255), random.nextInt(255)))
                .withColor(Color.fromRGB(random.nextInt(255), random.nextInt(255), random.nextInt(255)))
                .withFade(Color.fromRGB(random.nextInt(255), random.nextInt(255), random.nextInt(255)))
                .build());
        return meta;
    }

    private FireworkEffect.Type getRandomFireworkType() {
        Random random = new Random();
        return FireworkEffect.Type.values()[random.nextInt(FireworkEffect.Type.values().length)];
    }
}
