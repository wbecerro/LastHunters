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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;
import wbe.lastHunters.LastHunters;
import wbe.lastHunters.config.entities.Chicken;
import wbe.lastHunters.config.entities.PoolMob;
import wbe.lastHunters.config.locations.BowSpot;
import wbe.lastHunters.config.locations.ChickenCannon;
import wbe.lastHunters.hooks.WorldGuardManager;
import wbe.lastHunters.items.CatalystType;
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

    public PoolMob searchPoolMob(String id) {
        for(PoolMob mob : LastHunters.config.poolMobs) {
            if(mob.getId().equalsIgnoreCase(id)) {
                return mob;
            }
        }

        return null;
    }

    public Chicken searchChicken(String id) {
        for(Chicken chicken : LastHunters.config.chickens) {
            if(chicken.getId().equalsIgnoreCase(id)) {
                return chicken;
            }
        }

        return null;
    }

    public CatalystType searchCatalyst(String id) {
        for(CatalystType catalystType : LastHunters.config.catalystTypes) {
            if(catalystType.getId().equalsIgnoreCase(id)) {
                return catalystType;
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

    public void spawnChickensFromCannons() {
        Random random = new Random();
        for(ChickenCannon cannon : LastHunters.config.cannons) {
            if(random.nextInt(100) > LastHunters.config.chickenCannonSuccessChance) {
                continue;
            }

            Location cannonLocation = cannon.getLocation().clone().add(0.5, 1, 0.5);
            Chicken randomChicken = getRandomChicken();
            LivingEntity chicken = (LivingEntity) cannonLocation.getWorld().spawnEntity(cannonLocation, EntityType.CHICKEN);
            if(randomChicken.isGlow()) {
                chicken.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, PotionEffect.INFINITE_DURATION,
                        1, false, false, false));
                if(randomChicken.getGlowColor() != null) {
                    LastHunters.teams.get(randomChicken).setColor(randomChicken.getGlowColor());
                    LastHunters.teams.get(randomChicken).addEntry(chicken.getUniqueId().toString());
                }
            }
            NamespacedKey mobKey = new NamespacedKey(plugin, "specialMob");
            NamespacedKey chickenKey = new NamespacedKey(plugin, "chicken");
            chicken.getPersistentDataContainer().set(mobKey, PersistentDataType.BOOLEAN, true);
            chicken.getPersistentDataContainer().set(chickenKey, PersistentDataType.STRING, randomChicken.getId());

            chicken.getEquipment().clear();
            chicken.getPassengers().clear();
            chicken.setCustomName(randomChicken.getName());
            chicken.setCanPickupItems(false);
            chicken.setCustomNameVisible(true);
            chicken.setRemoveWhenFarAway(false);

            double vectorX = Math.random() - 0.2;
            double vectorY = 2;
            double vectorZ = ((Math.random() * (3 - 1)) + 1) * -1;
            Vector launch = new Vector(vectorX, vectorY, vectorZ);
            chicken.setVelocity(launch);
        }
    }

    public void registerAllTeams(Scoreboard scoreboard) {
        for(Chicken chicken : LastHunters.config.chickens) {
            if(!chicken.isGlow()) {
                continue;
            }

            if(chicken.getGlowColor() == null) {
                continue;
            }

            Team team;
            if(scoreboard.getTeam(chicken.getId()) == null) {
                team = scoreboard.registerNewTeam(chicken.getId());
            } else {
                team = scoreboard.getTeam(chicken.getId());
            }

            LastHunters.teams.put(chicken, team);
        }
    }

    public BowSpot getValidSpot(Player player) {
        for(BowSpot spot : LastHunters.config.spots) {
            if(spot.isPlayerHere(player)) {
                return spot;
            }
        }

        return null;
    }

    private Chicken getRandomChicken() {
        Random random = new Random();
        int randomNumber = random.nextInt(LastHunters.config.maxChickensWeight);
        int weight = 0;
        Set<Chicken> chickens = LastHunters.config.chickens;
        Chicken lastChicken = null;

        for(Chicken chicken : chickens) {
            lastChicken = chicken;
            weight += chicken.getWeight();
            if(randomNumber < weight) {
                return chicken;
            }
        }

        return lastChicken;
    }

    private PoolMob getRandomPoolMob() {
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

    public int findLine(ItemStack item, String line) {
        List<String> lore = item.getItemMeta().getLore();
        int size = lore.size();
        for(int i=0;i<size;i++) {
            if(lore.get(i).contains(line)) {
                return i;
            }
        }

        return -1;
    }
}
