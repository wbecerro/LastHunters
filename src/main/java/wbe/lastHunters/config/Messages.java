package wbe.lastHunters.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class Messages {

    private FileConfiguration config;

    public String noPermission;
    public String notEnoughArgs;
    public String undefinedHead;
    public String noReward;
    public String wrongHeadLocation;
    public String headPlaced;
    public String bossSpawning;
    public String bossSpawned;
    public String lowRodTitle;
    public String lowRodSubtitle;
    public String cannotUseBow;
    public String bowBroken;
    public String lowBowUses;
    public String doubleDrop;
    public String rodArguments;
    public String bowArguments;
    public String catalystArguments;
    public String fishChanceArguments;
    public String doubleChanceArguments;
    public String addRewardArguments;
    public String addedFishChance;
    public String addedDoubleChance;
    public String addedReward;
    public String reload;
    public List<String> failedCatch;
    public List<String> help;

    public Messages(FileConfiguration config) {
        this.config = config;

        noPermission = config.getString("Messages.noPermission").replace("&", "§");
        notEnoughArgs = config.getString("Messages.notEnoughArgs").replace("&", "§");
        undefinedHead = config.getString("Messages.undefinedHead").replace("&", "§");
        noReward = config.getString("Messages.noReward").replace("&", "§");
        wrongHeadLocation = config.getString("Messages.wrongHeadLocation").replace("&", "§");
        headPlaced = config.getString("Messages.headPlaced").replace("&", "§");
        bossSpawning = config.getString("Messages.bossSpawning").replace("&", "§");
        bossSpawned = config.getString("Messages.bossSpawned").replace("&", "§");
        lowRodTitle = config.getString("Messages.lowRodTitle").replace("&", "§");
        lowRodSubtitle = config.getString("Messages.lowRodSubtitle").replace("&", "§");
        cannotUseBow = config.getString("Messages.cannotUseBow").replace("&", "§");
        bowBroken = config.getString("Messages.bowBroken").replace("&", "§");
        lowBowUses = config.getString("Messages.lowBowUses").replace("&", "§");
        doubleDrop = config.getString("Messages.doubleDrop").replace("&", "§");
        rodArguments = config.getString("Messages.rodArguments").replace("&", "§");
        bowArguments = config.getString("Messages.bowArguments").replace("&", "§");
        catalystArguments = config.getString("Messages.catalystArguments").replace("&", "§");
        fishChanceArguments = config.getString("Messages.fishChanceArguments").replace("&", "§");
        doubleChanceArguments = config.getString("Messages.doubleChanceArguments").replace("&", "§");
        addRewardArguments = config.getString("Messages.addRewardArguments").replace("&", "§");
        addedFishChance = config.getString("Messages.addedFishChance").replace("&", "§");
        addedDoubleChance = config.getString("Messages.addedDoubleChance").replace("&", "§");
        addedReward = config.getString("Messages.addedReward").replace("&", "§");
        reload = config.getString("Messages.reload").replace("&", "§");
        failedCatch = config.getStringList("Messages.failedCatch");
        help = config.getStringList("Messages.help");
    }
}
