package wbe.lastHunters.items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import wbe.lastHunters.LastHunters;

import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

public class Catalyst extends ItemStack {

    private LastHunters plugin = LastHunters.getInstance();

    public Catalyst(CatalystType type) {
        super(Material.PLAYER_HEAD);

        ItemMeta meta;
        if(hasItemMeta()) {
            meta = getItemMeta();
        } else {
            meta = Bukkit.getItemFactory().getItemMeta(Material.PLAYER_HEAD);
        }

        SkullMeta skullMeta = (SkullMeta) meta;
        skullMeta.setDisplayName(type.getName());

        ArrayList<String> lore = new ArrayList<>();
        for(String line : type.getLore()) {
            lore.add(line.replace("&", "§"));
        }
        skullMeta.setLore(lore);

        PlayerProfile playerProfile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = playerProfile.getTextures();
        URL url;
        try {
            url = new URL(type.getSignature());
        } catch(Exception ex) {
            throw new RuntimeException("Error while loading skin " + type.getSignature());
        }
        textures.setSkin(url);
        playerProfile.setTextures(textures);
        skullMeta.setOwnerProfile(playerProfile);

        setItemMeta(skullMeta);

        setKeys(type);
    }

    private void setKeys(CatalystType type) {
        ItemMeta meta = getItemMeta();
        NamespacedKey catalystKey = new NamespacedKey(plugin, "catalyst");

        meta.getPersistentDataContainer().set(catalystKey, PersistentDataType.STRING, type.getId());

        setItemMeta(meta);
    }
}
