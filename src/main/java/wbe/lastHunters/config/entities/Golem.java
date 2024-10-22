package wbe.lastHunters.config.entities;

import net.minecraft.world.entity.Mob;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import wbe.lastHunters.rarities.Rarity;

import java.util.*;

public class Golem {

    private String id;

    private String name;

    private int weight;

    private int hp;

    private EntityType type;

    private boolean glow;

    private ChatColor glowingColor;

    private HashMap<Rarity, Integer> rarities;

    private int maxWeight = 0;

    public Golem(String id, String name, int weight, int hp, EntityType type, boolean glow, ChatColor glowingColor,
                 HashMap<Rarity, Integer> rarities) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.hp = hp;
        this.type = type;
        this.glow = glow;
        this.glowingColor = glowingColor;
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

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public EntityType getType() {
        return type;
    }

    public void setType(EntityType type) {
        this.type = type;
    }

    public boolean isGlow() {
        return glow;
    }

    public void setGlow(boolean glow) {
        this.glow = glow;
    }

    public ChatColor getGlowingColor() {
        return glowingColor;
    }

    public void setGlowingColor(ChatColor glowingColor) {
        this.glowingColor = glowingColor;
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
