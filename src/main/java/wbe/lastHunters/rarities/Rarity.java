package wbe.lastHunters.rarities;

import java.util.List;
import java.util.Random;

public class Rarity {

    private String id;

    private String prefix;

    private List<Reward> rewards;

    private int rewardsSize;

    private String broadcast;

    private int fireworks;

    public Rarity(String id, String prefix, List<Reward> rewards, String broadcast, int fireworks) {
        this.id = id;
        this.prefix = prefix;
        this.rewards = rewards;
        rewardsSize = rewards.size();
        this.broadcast = broadcast;
        this.fireworks = fireworks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public void setRewards(List<Reward> rewards) {
        this.rewards = rewards;
        rewardsSize = rewards.size();
    }

    public String getBroadcast() {
        return broadcast;
    }

    public void setBroadcast(String broadcast) {
        this.broadcast = broadcast;
    }

    public int getFireworks() {
        return fireworks;
    }

    public void setFireworks(int fireworks) {
        this.fireworks = fireworks;
    }

    public Reward getRandomReward() {
        Random random = new Random();
        return rewards.get(random.nextInt(rewardsSize));
    }
}
