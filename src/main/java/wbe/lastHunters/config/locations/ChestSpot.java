package wbe.lastHunters.config.locations;

import org.bukkit.Location;
import org.bukkit.Material;
import wbe.lastHunters.rarities.Rarity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

public class ChestSpot {

    private String id;

    private Location location;

    private Material material;;

    private int minRewards;

    private int maxRewards;

    private HashMap<Rarity, Integer> rarities;

    private int maxWeight = 0;

    public ChestSpot(String id, Material material, Location location, int minRewards, int maxRewards, HashMap<Rarity,
            Integer> rarities) {
        this.id = id;
        this.location = location;
        this.material = material;
        this.minRewards = minRewards;
        this.maxRewards = maxRewards;
        this.rarities = rarities;
        calculateMaxWeight();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getMinRewards() {
        return minRewards;
    }

    public void setMinRewards(int minRewards) {
        this.minRewards = minRewards;
    }

    public int getMaxRewards() {
        return maxRewards;
    }

    public void setMaxRewards(int maxRewards) {
        this.maxRewards = maxRewards;
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
