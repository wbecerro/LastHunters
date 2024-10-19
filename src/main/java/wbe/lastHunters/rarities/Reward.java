package wbe.lastHunters.rarities;

import org.bukkit.inventory.ItemStack;

public class Reward {

    private String id;

    private String message;

    private ItemStack item;

    public Reward(String id, String message, ItemStack item) {
        this.id = id;
        this.message = message;
        this.item = item;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }
}
