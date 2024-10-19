package wbe.lastHunters.config.entities;

import org.bukkit.entity.EntityType;
import wbe.lastHunters.rarities.Rarity;

import java.util.*;

public class PoolMob {

    private String id;

    private int weight;

    private EntityType type;

    private int catchChance;

    private String name;

    private HashMap<Rarity, Integer> rarities;

    private int maxWeight = 0;

    public static HashMap<String, Integer> spawnedMobs = new HashMap<>();

    public PoolMob(String id, int weight, EntityType type, int catchChance, String name, HashMap<Rarity, Integer> rarities) {
        this.id = id;
        this.weight = weight;
        this.type = type;
        this.catchChance = catchChance;
        this.name = name;
        this.rarities = rarities;
        calculateMaxWeight();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public EntityType getType() {
        return type;
    }

    public void setType(EntityType type) {
        this.type = type;
    }

    public int getCatchChance() {
        return catchChance;
    }

    public void setCatchChance(int catchChance) {
        this.catchChance = catchChance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<Rarity, Integer> getRarities() {
        return rarities;
    }

    public void setRarities(HashMap<Rarity, Integer> rarities) {
        this.rarities = rarities;
        calculateMaxWeight();
    }

    public Rarity getRandomRarity() {
        Random random = new Random();
        int randomNumber = random.nextInt(maxWeight);
        int weight = 0;
        Set<Rarity> keys = rarities.keySet();
        Rarity lastRarity = null;
        for(Rarity rarity : keys) {
            lastRarity = rarity;
            weight += rarities.get(rarity);
            if(randomNumber < weight) {
                return rarity;
            }
        }

        return lastRarity;
    }

    private void calculateMaxWeight() {
        Collection<Integer> weights = rarities.values();
        maxWeight = 0;
        for(Integer weight : weights) {
            maxWeight += weight;
        }
    }
}
