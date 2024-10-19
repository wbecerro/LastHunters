package wbe.lastHunters.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import wbe.lastHunters.LastHunters;

import java.util.ArrayList;

public class Bow extends ItemStack {

    private LastHunters plugin = LastHunters.getInstance();

    public Bow(int uses) {
        super(Material.BOW);

        ItemMeta meta;
        if(hasItemMeta()) {
            meta = getItemMeta();
        } else {
            meta = Bukkit.getItemFactory().getItemMeta(Material.BOW);
        }

        meta.setDisplayName(LastHunters.config.bowName);

        ArrayList<String> lore = new ArrayList<>();
        for(String line : LastHunters.config.bowLore) {
            lore.add(line.replace("&", "§"));
        }

        lore.add(LastHunters.config.usesLore.replace("%uses%", String.valueOf(uses)));
        meta.setLore(lore);
        meta.addEnchant(Enchantment.INFINITY, 1, true);
        setItemMeta(meta);

        setKeys(uses);
    }

    private void setKeys(int uses) {
        ItemMeta meta = getItemMeta();
        NamespacedKey bowKey = new NamespacedKey(plugin, "specialBow");
        NamespacedKey usesKey = new NamespacedKey(plugin, "bowUses");

        meta.getPersistentDataContainer().set(bowKey, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(usesKey, PersistentDataType.INTEGER, uses);

        setItemMeta(meta);
    }
}
