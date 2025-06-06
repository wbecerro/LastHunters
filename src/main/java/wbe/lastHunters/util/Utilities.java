package wbe.lastHunters.util;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.MobExecutor;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.SnowGolem;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftMob;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftSnowman;
import org.bukkit.entity.*;
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
import wbe.lastHunters.config.entities.Boss;
import wbe.lastHunters.config.entities.Chicken;
import wbe.lastHunters.config.entities.Golem;
import wbe.lastHunters.config.entities.PoolMob;
import wbe.lastHunters.config.locations.*;
import wbe.lastHunters.hooks.WorldGuardManager;
import wbe.lastHunters.items.CatalystType;
import wbe.lastHunters.nms.goals.MoveToPositionGoal;
import wbe.lastHunters.rarities.Rarity;
import wbe.lastHunters.rarities.Reward;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

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

    public int getPlayerDoubleChance(Player player) {
        int chance = 0;

        PlayerInventory inventory = player.getInventory();
        ItemStack mainHand = inventory.getItemInMainHand();
        ItemStack offHand = inventory.getItemInOffHand();
        ItemStack[] armor = inventory.getArmorContents();

        if(!mainHand.getType().equals(Material.AIR)) {
            chance += getItemDoubleChance(mainHand);
        }

        if(!offHand.getType().equals(Material.AIR)) {
            chance += getItemDoubleChance(offHand);
        }

        for(ItemStack item : armor) {
            if(item == null) {
                continue;
            }
            chance += getItemDoubleChance(item);
        }

        return chance;
    }

    private int getItemDoubleChance(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if(meta == null) {
            return 0;
        }

        NamespacedKey baseDoubleKey = new NamespacedKey(plugin, "doubleChance");
        if(meta.getPersistentDataContainer().has(baseDoubleKey)) {
            return meta.getPersistentDataContainer().get(baseDoubleKey, PersistentDataType.INTEGER);
        }

        return 0;
    }

    public void addRodChance(ItemStack item, int chance) {
        NamespacedKey baseItemKey = new NamespacedKey(plugin, "rodChance");
        String loreLine = LastHunters.config.rodChanceLore
                .replace("%rodChance%", String.valueOf(chance));
        ItemMeta meta = item.getItemMeta();

        if(meta == null) {
            meta = Bukkit.getItemFactory().getItemMeta(item.getType());
        }

        List<String> lore = new ArrayList<>();
        if(meta.hasLore()) {
            lore = meta.getLore();
        }

        lore.add(loreLine);
        meta.setLore(lore);

        meta.getPersistentDataContainer().set(baseItemKey, PersistentDataType.INTEGER, chance);
        item.setItemMeta(meta);
    }

    public void addDoubleChance(ItemStack item, int chance) {
        NamespacedKey baseItemKey = new NamespacedKey(plugin, "doubleChance");
        String loreLine = LastHunters.config.doubleChanceLore
                .replace("%doubleChance%", String.valueOf(chance));
        ItemMeta meta = item.getItemMeta();

        if(meta == null) {
            meta = Bukkit.getItemFactory().getItemMeta(item.getType());
        }

        List<String> lore = new ArrayList<>();
        if(meta.hasLore()) {
            lore = meta.getLore();
        }

        lore.add(loreLine);
        meta.setLore(lore);

        meta.getPersistentDataContainer().set(baseItemKey, PersistentDataType.INTEGER, chance);
        item.setItemMeta(meta);
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

    public ChickenCannon searchChickenCannon(String id) {
        for(ChickenCannon chickenCannon : LastHunters.config.cannons) {
            if(chickenCannon.getId().equalsIgnoreCase(id)) {
                return chickenCannon;
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

    public CatalystSpot searchCatalystSpot(String id) {
        for(CatalystSpot catalystSpot : LastHunters.config.catalystSpots) {
            if(catalystSpot.getId().equalsIgnoreCase(id)) {
                return catalystSpot;
            }
        }

        return null;
    }

    public Golem searchGolem(String id) {
        for(Golem golem : LastHunters.config.golems) {
            if(golem.getId().equalsIgnoreCase(id)) {
                return golem;
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
        ItemStack item = reward.getItem();
        if(!rarity.getBroadcast().isEmpty()) {
            Bukkit.getServer().broadcastMessage(rarity.getBroadcast().replace("%player%", player.getName()));
        }

        if(rarity.getFireworks() != -1) {
            for(int i=0;i<rarity.getFireworks();i++) {
                Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK_ROCKET);
                firework.setFireworkMeta(getRandomFirework(firework));
            }
        }

        if(player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItem(player.getLocation(), item);
            player.sendTitle(LastHunters.messages.inventoryFullTitle, "", 10, LastHunters.config.titleTime * 20, 20);
        } else {
            player.getInventory().addItem(item);
        }

        String message = rarity.getPrefix() + reward.getMessage().replace("%item%", item.getItemMeta().getDisplayName());
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
            if(!cannon.getLocation().getChunk().isLoaded()) {
                continue;
            }

            if(ChickenCannon.spawnedChickens.get(cannon) >= LastHunters.config.maxCannonChickens) {
                continue;
            }

            if(random.nextInt(100) > LastHunters.config.chickenCannonSuccessChance) {
                continue;
            }

            Location cannonLocation = cannon.getLocation().clone().add(0.5, 1, 0.5);
            Chicken randomChicken = getRandomChicken();
            LivingEntity chicken = (LivingEntity) cannonLocation.getWorld().spawnEntity(cannonLocation, EntityType.CHICKEN);
            ChickenCannon.spawnedChickens.put(cannon, ChickenCannon.spawnedChickens.get(cannon) + 1);
            if(randomChicken.isGlow()) {
                chicken.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, PotionEffect.INFINITE_DURATION,
                        1, false, false, false));
                if(randomChicken.getGlowColor() != null) {
                    LastHunters.chickenTeams.get(randomChicken).setColor(randomChicken.getGlowColor());
                    LastHunters.chickenTeams.get(randomChicken).addEntry(chicken.getUniqueId().toString());
                }
            }
            NamespacedKey mobKey = new NamespacedKey(plugin, "specialMob");
            NamespacedKey chickenKey = new NamespacedKey(plugin, "chicken");
            NamespacedKey cannonKey = new NamespacedKey(plugin, "cannon");
            chicken.getPersistentDataContainer().set(mobKey, PersistentDataType.BOOLEAN, true);
            chicken.getPersistentDataContainer().set(chickenKey, PersistentDataType.STRING, randomChicken.getId());
            chicken.getPersistentDataContainer().set(cannonKey, PersistentDataType.STRING, cannon.getId());

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

            LastHunters.chickenTeams.put(chicken, team);
        }

        for(Golem golem : LastHunters.config.golems) {
            if(!golem.isGlow()) {
                continue;
            }

            if(golem.getGlowingColor() == null) {
                continue;
            }

            Team team;
            if(scoreboard.getTeam(golem.getId()) == null) {
                team = scoreboard.registerNewTeam(golem.getId());
            } else {
                team = scoreboard.getTeam(golem.getId());
            }

            LastHunters.golemTeams.put(golem, team);
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

    public DestroyerSpot getValidDestroyerSpot(Player player) {
        for(DestroyerSpot spot : LastHunters.config.destroyerSpots) {
            if(spot.isPlayerHere(player)) {
                return spot;
            }
        }

        return null;
    }

    public void placeCatalystInteraction(CatalystType type, Player player, Location location) {
        CatalystSpot.catalystPlaced += 1;
        Bukkit.broadcastMessage(LastHunters.messages.headPlaced
                .replace("%player%", player.getName())
                .replace("%bossHead_playername%", type.getName())
                .replace("%current_heads_placed%", String.valueOf(CatalystSpot.catalystPlaced))
                .replace("%max%", String.valueOf(LastHunters.config.catalystSpots.size())));
        World world = location.getWorld();
        world.strikeLightningEffect(location);
        world.createExplosion(location, 1.0F);
        if(CatalystSpot.catalystPlaced >= LastHunters.config.catalystSpots.size()) {
            spawnBoss();
            CatalystSpot.catalystPlaced = 0;
        }
    }

    public void spawnBoss() {
        int iterations = 0;
        for(CatalystSpot catalystSpot : LastHunters.config.catalystSpots) {
            iterations++;
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    Location location = catalystSpot.getLocation();
                    World world = location.getWorld();
                    world.strikeLightningEffect(location);
                    world.createExplosion(location, 1.0F);
                    world.getBlockAt(location).setType(Material.AIR);
                    world.playSound(location, LastHunters.config.headDisappearSound, 1.0F, 1.0F);
                }
            }, 15L * iterations);
        }

        Bukkit.broadcastMessage(LastHunters.messages.bossSpawning);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                MobExecutor mobExecutor = MythicBukkit.inst().getMobManager();
                Boss boss = getRandomBoss();
                MythicMob mythicMob = mobExecutor.getMythicMob(boss.getName()).get();
                mobExecutor.spawnMob(boss.getName(), LastHunters.config.bossLocation);
                LastHunters.config.bossLocation.getWorld().playSound(LastHunters.config.bossLocation, LastHunters.config.bossAppearSound, 1.0F, 1.0F);
                Bukkit.broadcastMessage(LastHunters.messages.bossSpawned.replace("%boss%", mythicMob.getDisplayName().get()));
            }
        }, 15L * iterations + 1);
    }

    public void addReward(String rarity, String id, ItemStack item) {
        FileConfiguration config = LastHunters.rewardsConfig;
        config.set("Rarities." + rarity + ".rewards." + id + ".message", "Default reward message");
        config.set("Rarities." + rarity + ".rewards." + id + ".item", item);
        try {
            config.save(plugin.rewardsConfigFile);
            plugin.reloadConfiguration();
        } catch(IOException e) {
            throw new RuntimeException("Error while saving the rewards config.");
        }
    }

    public void fillChest() {
        ChestSpot chest = getRandomChest();
        Random random = new Random();
        int rewards = random.nextInt(chest.getMaxRewards() - chest.getMinRewards()) + chest.getMinRewards();
        Location location = chest.getLocation();
        World world = location.getWorld();
        Block block = world.getBlockAt(location);
        for(int i=0;i<rewards;i++) {
            Rarity rarity = chest.getRandomRarity();
            ItemStack reward = rarity.getRandomReward().getItem();
            Container container = null;
            switch(chest.getMaterial()) {
                case CHEST:
                    container = (Chest) block.getState();
                    break;
                case BARREL:
                    container = (Barrel) block.getState();
                    break;
                default:
                    container = (ShulkerBox) block.getState();
                    break;
            }
            container.getInventory().addItem(reward);
        }

        world.strikeLightningEffect(location);
    }

    public void spawnGolems() {
        if(GolemSpot.spawnedGolems >= LastHunters.config.maxGolems) {
            return;
        }

        Random random = new Random();
        int randomPosition = random.nextInt(LastHunters.config.maxGolemSpots) + 1;
        GolemSpot golemSpot = LastHunters.config.golemSpots.get(randomPosition);
        Location location = golemSpot.getLocation().clone();
        if(!location.getChunk().isLoaded()) {
            return;
        }

        NamespacedKey mobKey = new NamespacedKey(plugin, "specialMob");
        NamespacedKey golemKey = new NamespacedKey(plugin, "golem");
        NamespacedKey hpKey = new NamespacedKey(plugin, "golemHP");

        Golem golem = getRandomGolem();
        Mob mob = (Mob) location.getWorld().spawnEntity(location, golem.getType());

        if(golem.isGlow()) {
            mob.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, PotionEffect.INFINITE_DURATION,
                    1, false, false, false));
            if(golem.getGlowingColor() != null) {
                LastHunters.golemTeams.get(golem).setColor(golem.getGlowingColor());
                LastHunters.golemTeams.get(golem).addEntry(mob.getUniqueId().toString());
            }
        }

        mob.getPersistentDataContainer().set(mobKey, PersistentDataType.BOOLEAN, true);
        mob.getPersistentDataContainer().set(golemKey, PersistentDataType.STRING, golem.getId());
        mob.getPersistentDataContainer().set(hpKey, PersistentDataType.INTEGER, golem.getHp());

        mob.getEquipment().clear();
        mob.setCustomName(golem.getName().replace("%hp%", LastHunters.config.golemHPIcon.repeat(golem.getHp())));
        mob.setCanPickupItems(false);
        mob.setCustomNameVisible(true);
        mob.setRemoveWhenFarAway(true);
        mob.getPassengers().clear();
        if(mob instanceof Ageable) {
            ((Ageable) mob).setAdult();
        }

        CraftMob craftMob = (CraftMob) mob;
        net.minecraft.world.entity.Mob nmsMob = craftMob.getHandle();
        Predicate<Goal> goalPredicate = i -> true;
        nmsMob.removeAllGoals(goalPredicate);
        nmsMob.goalSelector.addGoal(1, new MoveToPositionGoal((PathfinderMob) nmsMob, golemSpot));
        GolemSpot.spawnedGolems++;
    }

    private Boss getRandomBoss() {
        Random random = new Random();
        int randomNumber = random.nextInt(LastHunters.config.maxBossesWeight);
        int weight = 0;
        Set<Boss> bosses = LastHunters.config.bosses;
        Boss lastBoss = null;

        for(Boss boss : bosses) {
            lastBoss = boss;
            weight += boss.getWeight();
            if(randomNumber < weight) {
                return boss;
            }
        }

        return lastBoss;
    }

    private ChestSpot getRandomChest() {
        Random random = new Random();
        return LastHunters.config.chests.get(random.nextInt(LastHunters.config.chests.size()));
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

    private Golem getRandomGolem() {
        Random random = new Random();
        int randomNumber = random.nextInt(LastHunters.config.maxGolemWeight);
        int weight = 0;
        Set<Golem> golems = LastHunters.config.golems;
        Golem lastGolem = null;

        for(Golem golem : golems) {
            lastGolem = golem;
            weight += golem.getWeight();
            if(randomNumber < weight) {
                return golem;
            }
        }

        return lastGolem;
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
