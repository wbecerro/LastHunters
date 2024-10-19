package wbe.lastHunters.config;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import wbe.lastHunters.LastHunters;
import wbe.lastHunters.config.entities.Chicken;
import wbe.lastHunters.config.entities.PoolMob;
import wbe.lastHunters.config.locations.BowSpot;
import wbe.lastHunters.config.locations.CatalystSpot;
import wbe.lastHunters.config.locations.ChestSpot;
import wbe.lastHunters.config.locations.ChickenCannon;
import wbe.lastHunters.items.CatalystType;
import wbe.lastHunters.rarities.Rarity;
import wbe.lastHunters.rarities.Reward;

import java.util.*;

public class Config {

    private FileConfiguration config;

    public int poolMobsRespawnTime;
    public int maxRegionMobs;
    public boolean enableChests;
    public boolean enableChickens;
    public int refillChestsTime;
    public Sound catchFailSound;
    public int titleTime;
    public int chickenCannonFireTime;
    public int chickenCannonSuccessChance;
    public List<String> enabledWorlds;

    public boolean rodGlow;
    public String rodName;
    public List<String> rodLore;

    public boolean bowGlow;
    public String bowName;
    public List<String> bowLore;

    public String rodChanceLore;

    public String bossName;
    public Location bossLocation;

    public int maxChickensWeight = 0;
    public int maxPoolWeight = 0;

    public HashMap<String, Rarity> rarities = new HashMap<>();
    public Set<ChickenCannon> cannons = new HashSet<>();
    public Set<ChestSpot> chests = new HashSet<>();
    public Set<BowSpot> spots = new HashSet<>();
    public Set<Chicken> chickens = new HashSet<>();
    public Set<PoolMob> poolMobs = new HashSet<>();
    public Set<CatalystSpot> catalystSpots = new HashSet<>();
    public List<CatalystType> catalystTypes = new ArrayList<>();

    public Config(FileConfiguration config) {
        this.config = config;

        poolMobsRespawnTime = config.getInt("Config.poolMobsRespawnTime");
        maxRegionMobs = config.getInt("Config.maxRegionMobs");
        enableChests = config.getBoolean("Config.enableChests");
        enableChickens = config.getBoolean("Config.enableChickens");
        refillChestsTime = config.getInt("Config.refillChestsTime");
        catchFailSound = Sound.valueOf(config.getString("Config.catchFailSound"));
        titleTime = config.getInt("Config.titleTime");
        chickenCannonFireTime = config.getInt("Config.chickenCannonFireTime");
        chickenCannonSuccessChance = config.getInt("Config.chickenCannonSuccessChance");
        enabledWorlds = config.getStringList("Config.enabledWorlds");

        rodGlow = config.getBoolean("Items.rod.glow");
        rodName = config.getString("Items.rod.name").replace("&", "§");
        rodLore = config.getStringList("Items.rod.lore");

        bowGlow = config.getBoolean("Items.bow.glow");
        bowName = config.getString("Items.bow.name").replace("&", "§");
        bowLore = config.getStringList("Items.bow.lore");

        rodChanceLore = config.getString("Items.rodChanceLore").replace("&", "§");

        bossName = config.getString("Boss.mob");
        World world = Bukkit.getWorld(config.getString("Boss.spawn.world"));
        double x = config.getDouble("Boss.spawn.x");
        double y = config.getDouble("Boss.spawn.y");
        double z = config.getDouble("Boss.spawn.z");
        bossLocation = new Location(world, x, y, z);

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
    }

    private void loadRarities() {
        FileConfiguration rewardsConfig = LastHunters.rewardsConfig;
        Set<String> configRarities = rewardsConfig.getConfigurationSection("Rarities").getKeys(false);
        for(String rarity : configRarities) {
            String prefix = rewardsConfig.getString("Rarities." + rarity + ".prefix").replace("&", "§");
            String broadcast = "";
            if(config.contains("Rarities." + rarity + ".broadcast")) {
                broadcast = config.getString("Rarities." + rarity + ".broadcast").replace("&", "§");
            }
            int fireworks = -1;
            if(config.contains("Rarities." + rarity + ".fireworks")) {
                fireworks = config.getInt("Rarities." + rarity + ".fireworks");
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
            String command = rewardsConfig.getString("Rarities." + rarity + ".rewards." + reward + ".command");
            rewards.add(new Reward(reward, message, command));
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
            cannons.add(new ChickenCannon(cannon, location));
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
            maxChickensWeight += weight;
            HashMap<Rarity, Integer> raritiesWeights = loadRarityWeights("Chickens." + chicken, config);
            chickens.add(new Chicken(chicken, name, weight, glow, raritiesWeights));
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
}
