package wbe.lastHunters;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import wbe.lastHunters.commands.CommandListener;
import wbe.lastHunters.config.Config;
import wbe.lastHunters.config.Messages;
import wbe.lastHunters.config.entities.Chicken;
import wbe.lastHunters.hooks.WorldGuardManager;
import wbe.lastHunters.listeners.EventListeners;
import wbe.lastHunters.util.Scheduler;
import wbe.lastHunters.util.Utilities;

import java.io.File;
import java.util.HashMap;

public final class LastHunters extends JavaPlugin {

    private FileConfiguration configuration;

    private File cannonConfigFile;
    public static FileConfiguration cannonConfig;

    private File chestsConfigFile;
    public static FileConfiguration chestsConfig;

    public File rewardsConfigFile;
    public static FileConfiguration rewardsConfig;

    private File spotsConfigFile;
    public static FileConfiguration spotsConfig;

    private CommandListener commandListener;

    private EventListeners eventListeners;

    public static Config config;

    public static Messages messages;

    private Scoreboard scoreboard;

    public static HashMap<Chicken, Team> teams = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        createCannonFile();
        createChestsFile();
        createRewardsFile();
        createSpotsFile();
        getLogger().info("LastHunters enabled correctly");
        reloadConfiguration();

        commandListener = new CommandListener();
        getCommand("lasthunters").setExecutor(this.commandListener);
        eventListeners = new EventListeners();
        eventListeners.initializeListeners();

        Scheduler.startSchedulers(configuration, this);
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        reloadConfig();
        new Utilities().removeAllSpecialMobs();
        getLogger().info("LastHunters disabled correctly");
    }

    public void onLoad() {
        WorldGuardManager.loadFlags();
    }

    public static LastHunters getInstance() {
        return getPlugin(LastHunters.class);
    }

    public void reloadConfiguration() {
        if(!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
        }
        reloadConfig();
        configuration = getConfig();
        createCannonFile();
        createChestsFile();
        createRewardsFile();
        createSpotsFile();
        messages = new Messages(configuration);
        config = new Config(configuration);
        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        new Utilities().registerAllTeams(scoreboard);
    }

    private void createCannonFile() {
        cannonConfigFile = new File(getDataFolder(), "cannon.yml");
        if(!cannonConfigFile.exists()) {
            cannonConfigFile.getParentFile().mkdirs();
            saveResource("cannon.yml", false);
        }

        cannonConfig = YamlConfiguration.loadConfiguration(cannonConfigFile);
    }

    private void createChestsFile() {
        chestsConfigFile = new File(getDataFolder(), "chests.yml");
        if(!chestsConfigFile.exists()) {
            chestsConfigFile.getParentFile().mkdirs();
            saveResource("chests.yml", false);
        }

        chestsConfig = YamlConfiguration.loadConfiguration(chestsConfigFile);
    }

    private void createRewardsFile() {
        rewardsConfigFile = new File(getDataFolder(), "rewards.yml");
        if(!rewardsConfigFile.exists()) {
            rewardsConfigFile.getParentFile().mkdirs();
            saveResource("rewards.yml", false);
        }

        rewardsConfig = YamlConfiguration.loadConfiguration(rewardsConfigFile);
    }

    private void createSpotsFile() {
        spotsConfigFile = new File(getDataFolder(), "spots.yml");
        if(!spotsConfigFile.exists()) {
            spotsConfigFile.getParentFile().mkdirs();
            saveResource("spots.yml", false);
        }

        spotsConfig = YamlConfiguration.loadConfiguration(spotsConfigFile);
    }
}
