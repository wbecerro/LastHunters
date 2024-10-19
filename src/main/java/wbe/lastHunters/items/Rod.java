package wbe.lastHunters.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import wbe.lastHunters.LastHunters;

import java.util.ArrayList;

public class Rod extends ItemStack {

    private LastHunters plugin = LastHunters.getInstance();

    public Rod(int rodChance) {
        super(Material.FISHING_ROD);

        ItemMeta meta;
        if(hasItemMeta()) {
            meta = getItemMeta();
        } else {
            meta = Bukkit.getItemFactory().getItemMeta(Material.FISHING_ROD);
        }

        meta.setDisplayName(LastHunters.config.rodName);

        ArrayList<String> lore = new ArrayList<>();
        for(String line : LastHunters.config.rodLore) {
            lore.add(line.replace("&", "§"));
        }

        lore.add(LastHunters.config.rodChanceLore.replace("%rodChance%", String.valueOf(rodChance)));
        meta.setLore(lore);
        setItemMeta(meta);

        setKeys(rodChance);
    }

    private void setKeys(int rodChance) {
        ItemMeta meta = getItemMeta();
        NamespacedKey rodKey = new NamespacedKey(plugin, "specialRod");
        NamespacedKey chanceKey = new NamespacedKey(plugin, "rodChance");

        meta.getPersistentDataContainer().set(rodKey, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(chanceKey, PersistentDataType.INTEGER, rodChance);

        setItemMeta(meta);
    }
}
