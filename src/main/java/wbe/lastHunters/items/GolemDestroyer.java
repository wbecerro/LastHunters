package wbe.lastHunters.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import wbe.lastHunters.LastHunters;

import java.util.ArrayList;

public class GolemDestroyer extends ItemStack {

    private LastHunters plugin = LastHunters.getInstance();

    public GolemDestroyer(int uses) {
        super(LastHunters.config.golemDestroyerMaterial);

        ItemMeta meta;
        if(hasItemMeta()) {
            meta = getItemMeta();
        } else {
            meta = Bukkit.getItemFactory().getItemMeta(LastHunters.config.golemDestroyerMaterial);
        }

        meta.setDisplayName(LastHunters.config.golemDestroyerName);

        ArrayList<String> lore = new ArrayList<>();
        for(String line : LastHunters.config.golemDestroyerLore) {
            lore.add(line.replace("&", "§"));
        }

        lore.add(LastHunters.config.golemDestroyerUsesLore.replace("%uses%", String.valueOf(uses)));
        meta.setLore(lore);
        meta.addEnchant(Enchantment.INFINITY, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        setItemMeta(meta);

        setKeys(uses);
    }

    private void setKeys(int uses) {
        ItemMeta meta = getItemMeta();
        NamespacedKey destroyerKey = new NamespacedKey(plugin, "golemDestroyer");
        NamespacedKey usesKey = new NamespacedKey(plugin, "destroyerUses");

        meta.getPersistentDataContainer().set(destroyerKey, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(usesKey, PersistentDataType.INTEGER, uses);

        setItemMeta(meta);
    }
}