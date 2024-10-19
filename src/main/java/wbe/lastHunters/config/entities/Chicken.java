package wbe.lastHunters.config.entities;

import org.bukkit.entity.LivingEntity;
import wbe.lastHunters.rarities.Rarity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

public class Chicken {

    public static Set<LivingEntity> chickens;

    private String id;

    private String name;

    private int weight;

    private boolean glow;

    private HashMap<Rarity, Integer> rarities;

    private int maxWeight = 0;

    public Chicken(String id, String name, int weight, boolean glow, HashMap<Rarity, Integer> rarities) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.glow = glow;
        this.rarities = rarities;
        calculateMaxWeight();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isGlow() {
        return glow;
    }

    public void setGlow(boolean glow) {
        this.glow = glow;
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
