package wbe.lastHunters.config;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import wbe.lastHunters.LastHunters;
import wbe.lastHunters.config.entities.Boss;
import wbe.lastHunters.config.entities.Chicken;
import wbe.lastHunters.config.entities.Golem;
import wbe.lastHunters.config.entities.PoolMob;
import wbe.lastHunters.config.locations.*;
import wbe.lastHunters.items.CatalystType;
import wbe.lastHunters.rarities.Rarity;
import wbe.lastHunters.rarities.Reward;

import java.util.*;

public class Config {

    private FileConfiguration config;

    public int poolMobsRespawnTime;
    public int maxRegionMobs;
    public int maxCannonChickens;
    public int maxGolems;
    public boolean enablePool;
    public boolean enableChests;
    public boolean enableChickens;
    public boolean enableGolems;
    public int refillChestsTime;
    public Sound catchFailSound;
    public int titleTime;
    public int chickenCannonFireTime;
    public int chickenCannonSuccessChance;
    public int lowRodUses;
    public List<String> enabledWorlds;

    public String golemHPIcon;
    public double golemSpeed;

    public String rodName;
    public List<String> rodLore;

    public String bowName;
    public List<String> bowLore;
    public String usesLore;

    public Material golemDestroyerMaterial;
    public String golemDestroyerName;
    public List<String> golemDestroyerLore;
    public String golemDestroyerUsesLore;

    public String rodChanceLore;
    public String doubleChanceLore;

    public Location bossLocation;
    public Sound headDisappearSound;
    public Sound bossAppearSound;

    public int maxChickensWeight = 0;
    public int maxPoolWeight = 0;
    public int maxGolemWeight = 0;
    public int maxGolemSpots = 0;
    public int maxBossesWeight = 0;

    public HashMap<String, Rarity> rarities = new HashMap<>();
    public Set<ChickenCannon> cannons = new HashSet<>();
    public List<ChestSpot> chests = new ArrayList<>();
    public Set<BowSpot> spots = new HashSet<>();
    public Set<Chicken> chickens = new HashSet<>();
    public Set<PoolMob> poolMobs = new HashSet<>();
    public Set<CatalystSpot> catalystSpots = new HashSet<>();
    public Set<CatalystType> catalystTypes = new HashSet<>();
    public Set<Golem> golems = new HashSet<>();
    public HashMap<Integer, GolemSpot> golemSpots = new HashMap<>();
    public Set<DestroyerSpot> destroyerSpots = new HashSet<>();
    public Set<Boss> bosses = new HashSet<>();

    public Config(FileConfiguration config) {
        this.config = config;

        poolMobsRespawnTime = config.getInt("Config.poolMobsRespawnTime");
        maxRegionMobs = config.getInt("Config.maxRegionMobs");
        maxCannonChickens = config.getInt("Config.maxCannonChickens");
        maxGolems = config.getInt("Config.maxGolems");
        enableChests = config.getBoolean("Config.enableChests");
        enablePool = config.getBoolean("Config.enablePool");
        enableChickens = config.getBoolean("Config.enableChickens");
        enableGolems = config.getBoolean("Config.enableGolems");
        refillChestsTime = config.getInt("Config.refillChestsTime");
        catchFailSound = Sound.valueOf(config.getString("Config.catchFailSound"));
        titleTime = config.getInt("Config.titleTime");
        chickenCannonFireTime = config.getInt("Config.chickenCannonFireTime");
        chickenCannonSuccessChance = config.getInt("Config.chickenCannonSuccessChance");
        lowRodUses = config.getInt("Config.lowRodUses");
        enabledWorlds = config.getStringList("Config.enabledWorlds");

        golemHPIcon = config.getString("Config.HPIcon");
        golemSpeed = config.getDouble("Config.golemSpeed");

        rodName = config.getString("Items.rod.name").replace("&", "§");
        rodLore = config.getStringList("Items.rod.lore");

        bowName = config.getString("Items.bow.name").replace("&", "§");
        bowLore = config.getStringList("Items.bow.lore");
        usesLore = config.getString("Items.bow.usesLore").replace("&", "§");

        golemDestroyerMaterial = Material.valueOf(config.getString("Items.golemDestroyer.material"));
        golemDestroyerName = config.getString("Items.golemDestroyer.name").replace("&", "§");
        golemDestroyerLore = config.getStringList("Items.golemDestroyer.lore");
        golemDestroyerUsesLore = config.getString("Items.golemDestroyer.usesLore").replace("&", "§");

        rodChanceLore = config.getString("Items.rodChanceLore").replace("&", "§");
        doubleChanceLore = config.getString("Items.doubleChanceLore").replace("&", "§");

        World world = Bukkit.getWorld(config.getString("Boss.spawn.world"));
        double x = config.getDouble("Boss.spawn.x");
        double y = config.getDouble("Boss.spawn.y");
        double z = config.getDouble("Boss.spawn.z");
        bossLocation = new Location(world, x, y, z);
        headDisappearSound = Sound.valueOf(config.getString("Boss.headDisappearSound"));
        bossAppearSound = Sound.valueOf(config.getString("Boss.bossAppearSound"));

        loadAllConfig();
    }

    private void loadAllConfig() {
        loadRarities();
        loadCannons();
        loadChests();
        loadSpots();
        loadChickens();
        loadPoolMobs();
        loadCatalystSpots();
        loadCatalystsTypes();
        loadGolemSpots();
        loadGolems();
        loadDestroyerSpots();
        loadBosses();
    }

    private void loadRarities() {
        FileConfiguration rewardsConfig = LastHunters.rewardsConfig;
        Set<String> configRarities = rewardsConfig.getConfigurationSection("Rarities").getKeys(false);
        for(String rarity : configRarities) {
            String prefix = rewardsConfig.getString("Rarities." + rarity + ".prefix").replace("&", "§");
            String broadcast = "";
            if(rewardsConfig.contains("Rarities." + rarity + ".broadcast")) {
                broadcast = rewardsConfig.getString("Rarities." + rarity + ".broadcast").replace("&", "§");
            }
            int fireworks = -1;
            if(rewardsConfig.contains("Rarities." + rarity + ".fireworks")) {
                fireworks = rewardsConfig.getInt("Rarities." + rarity + ".fireworks");
            }
            List<Reward> rewards = loadRewards(rarity);
            rarities.put(rarity, new Rarity(rarity, prefix, rewards, broadcast, fireworks));
        }
    }

    private List<Reward> loadRewards(String rarity) {
        FileConfiguration rewardsConfig = LastHunters.rewardsConfig;
        Set<String> configRewards = rewardsConfig.getConfigurationSection("Rarities." + rarity + ".rewards").getKeys(false);
        List<Reward> rewards = new ArrayList<>();
        for(String reward : configRewards) {
            String message = rewardsConfig.getString("Rarities." + rarity + ".rewards." + reward + ".message").replace("&", "§");
            ItemStack item = rewardsConfig.getItemStack("Rarities." + rarity + ".rewards." + reward + ".item");
            rewards.add(new Reward(reward, message, item));
        }

        return rewards;
    }

    private void loadCannons() {
        FileConfiguration cannonConfig = LastHunters.cannonConfig;
        Set<String> configCannons = cannonConfig.getConfigurationSection("Cannons").getKeys(false);
        for(String cannon : configCannons) {
            World world = Bukkit.getWorld(cannonConfig.getString("Cannons." + cannon + ".world"));
            double x = cannonConfig.getDouble("Cannons." + cannon + ".x");
            double y = cannonConfig.getDouble("Cannons." + cannon + ".y");
            double z = cannonConfig.getDouble("Cannons." + cannon + ".z");
            Location location = new Location(world, x, y, z);
            ChickenCannon chickenCannon = new ChickenCannon(cannon, location);
            cannons.add(chickenCannon);
            ChickenCannon.spawnedChickens.put(chickenCannon, 0);
        }
    }

    private void loadChests() {
        FileConfiguration chestsConfig = LastHunters.chestsConfig;
        Set<String> configChests = chestsConfig.getConfigurationSection("Chests").getKeys(false);
        for(String chest : configChests) {
            Material type = Material.valueOf(chestsConfig.getString("Chests." + chest + ".type"));
            World world = Bukkit.getWorld(chestsConfig.getString("Chests." + chest + ".world"));
            double x = chestsConfig.getDouble("Chests." + chest + ".x");
            double y = chestsConfig.getDouble("Chests." + chest + ".y");
            double z = chestsConfig.getDouble("Chests." + chest + ".z");
            Location location = new Location(world, x, y, z);
            int minRewards = chestsConfig.getInt("Chests." + chest + ".min_rewards");
            int maxRewards = chestsConfig.getInt("Chests." + chest + ".max_rewards");
            HashMap<Rarity, Integer> raritiesWeights = loadRarityWeights("Chests." + chest, chestsConfig);
            chests.add(new ChestSpot(chest, type, location, minRewards, maxRewards, raritiesWeights));
        }
    }

    private HashMap<Rarity, Integer> loadRarityWeights(String configSection, FileConfiguration configuration) {
        HashMap<Rarity, Integer> raritiesWeights = new HashMap<>();
        Set<String> section = configuration.getConfigurationSection(configSection + ".rewards").getKeys(false);
        for(String sectionString : section) {
            raritiesWeights.put(rarities.get(sectionString), configuration.getInt(configSection + ".rewards." + sectionString));
        }

        return raritiesWeights;
    }

    private void loadSpots() {
        FileConfiguration spotsConfig = LastHunters.spotsConfig;
        Set<String> configSpots = spotsConfig.getConfigurationSection("Spots").getKeys(false);
        for(String spot : configSpots) {
            World world = Bukkit.getWorld(spotsConfig.getString("Spots." + spot + ".world"));
            double x = spotsConfig.getDouble("Spots." + spot + ".x");
            double y = spotsConfig.getDouble("Spots." + spot + ".y");
            double z = spotsConfig.getDouble("Spots." + spot + ".z");
            Location location = new Location(world, x, y, z);
            spots.add(new BowSpot(spot, location));
        }
    }

    private void loadChickens() {
        Set<String> configChickens = config.getConfigurationSection("Chickens").getKeys(false);
        for(String chicken : configChickens) {
            String name = config.getString("Chickens." + chicken + ".name").replace("&", "§");
            int weight = config.getInt("Chickens." + chicken + ".weight");
            boolean glow = config.getBoolean("Chickens." + chicken + ".glow");
            ChatColor glowColor = null;
            if(config.contains("Chickens." + chicken + ".glowingColor")) {
                glowColor = ChatColor.valueOf(config.getString("Chickens." + chicken + ".glowingColor"));
            }
            maxChickensWeight += weight;
            HashMap<Rarity, Integer> raritiesWeights = loadRarityWeights("Chickens." + chicken, config);
            chickens.add(new Chicken(chicken, name, weight, glow, glowColor, raritiesWeights));
        }
    }

    private void loadPoolMobs() {
        Set<String> configMobs = config.getConfigurationSection("Pool").getKeys(false);
        for(String mob : configMobs) {
            String name = config.getString("Pool." + mob + ".name").replace("&", "§");
            int weight = config.getInt("Pool." + mob + ".weight");
            maxPoolWeight += weight;
            EntityType entityType = EntityType.valueOf(config.getString("Pool." + mob + ".entity"));
            int catchChance = config.getInt("Pool." + mob + ".catchChance");
            HashMap<Rarity, Integer> raritiesWeights = loadRarityWeights("Pool." + mob, config);
            poolMobs.add(new PoolMob(mob, weight, entityType, catchChance, name, raritiesWeights));
        }
    }

    private void loadCatalystSpots() {
        Set<String> configHeads = config.getConfigurationSection("Heads").getKeys(false);
        for(String head : configHeads) {
            World world = Bukkit.getWorld(config.getString("Heads." + head + ".location.world"));
            double x = config.getDouble("Heads." + head + ".location.x");
            double y = config.getDouble("Heads." + head + ".location.y");
            double z = config.getDouble("Heads." + head + ".location.z");
            Location location = new Location(world, x, y, z);
            catalystSpots.add(new CatalystSpot(head, location));
        }
    }

    private void loadCatalystsTypes() {
        Set<String> configHeads = config.getConfigurationSection("Heads").getKeys(false);
        for(String head : configHeads) {
            String name = config.getString("Heads." + head + ".name").replace("&", "§");
            List<String> lore = config.getStringList("Heads." + head + ".lore");
            String signature = config.getString("Heads." + head + ".signature");
            catalystTypes.add(new CatalystType(head, signature, name, lore));
        }
    }

    private void loadGolemSpots() {
        FileConfiguration golemConfig = LastHunters.golemsConfig;
        Set<String> configCannons = golemConfig.getConfigurationSection("Spots").getKeys(false);
        int position = 0;
        for(String golem : configCannons) {
            position++;
            World world = Bukkit.getWorld(golemConfig.getString("Spots." + golem + ".world"));
            double x = golemConfig.getDouble("Spots." + golem + ".x");
            double y = golemConfig.getDouble("Spots." + golem + ".y");
            double z = golemConfig.getDouble("Spots." + golem + ".z");
            Location location = new Location(world, x, y, z);
            GolemSpot golemSpot = new GolemSpot(golem, position, location);
            golemSpots.put(position, golemSpot);
        }
        maxGolemSpots = position;
    }

    private void loadGolems() {
        Set<String> configGolems = config.getConfigurationSection("Golems").getKeys(false);
        for(String golem : configGolems) {
            String name = config.getString("Golems." + golem + ".name").replace("&", "§");
            int weight = config.getInt("Golems." + golem + ".weight");
            int hp = config.getInt("Golems." + golem + ".hp");
            EntityType type = EntityType.valueOf(config.getString("Golems." + golem + ".entity"));
            boolean glow = config.getBoolean("Golems." + golem + ".glow");
            ChatColor glowColor = null;
            if(config.contains("Golems." + golem + ".glowingColor")) {
                glowColor = ChatColor.valueOf(config.getString("Golems." + golem + ".glowingColor"));
            }
            maxGolemWeight += weight;
            HashMap<Rarity, Integer> raritiesWeights = loadRarityWeights("Golems." + golem, config);
            golems.add(new Golem(golem, name, weight, hp, type, glow, glowColor, raritiesWeights));
        }
    }

    private void loadDestroyerSpots() {
        FileConfiguration spotsConfig = LastHunters.spotsConfig;
        Set<String> configSpots = spotsConfig.getConfigurationSection("Golems").getKeys(false);
        for(String spot : configSpots) {
            World world = Bukkit.getWorld(spotsConfig.getString("Golems." + spot + ".world"));
            double x = spotsConfig.getDouble("Golems." + spot + ".x");
            double y = spotsConfig.getDouble("Golems." + spot + ".y");
            double z = spotsConfig.getDouble("Golems." + spot + ".z");
            Location location = new Location(world, x, y, z);
            destroyerSpots.add(new DestroyerSpot(spot, location));
        }
    }

    private void loadBosses() {
        Set<String> configBosses = config.getConfigurationSection("Boss.mobs").getKeys(false);
        for(String boss : configBosses) {
            String name = config.getString("Boss.mobs." + boss + ".name").replace("&", "§");
            int weight = config.getInt("Boss.mobs." + boss + ".weight");
            maxBossesWeight += weight;
            bosses.add(new Boss(boss, name, weight));
        }
    }
}
