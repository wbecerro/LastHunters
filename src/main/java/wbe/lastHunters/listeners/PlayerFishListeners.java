package wbe.lastHunters.listeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.persistence.PersistentDataType;
import wbe.lastHunters.LastHunters;
import wbe.lastHunters.config.entities.PoolMob;
import wbe.lastHunters.util.Utilities;

import java.util.Random;

public class PlayerFishListeners implements Listener {

    private LastHunters plugin = LastHunters.getInstance();

    private Utilities utilities = new Utilities();

    @EventHandler(priority = EventPriority.NORMAL)
    public void catchPoolMobOnFishing(PlayerFishEvent event) {
        if(!event.getState().equals(PlayerFishEvent.State.CAUGHT_ENTITY)) {
            return;
        }

        if(!(event.getCaught() instanceof LivingEntity)) {
            return;
        }

        LivingEntity mob = (LivingEntity) event.getCaught();
        NamespacedKey poolKey = new NamespacedKey(plugin, "poolMob");
        NamespacedKey regionKey = new NamespacedKey(plugin, "mobRegion");
        if(!mob.getPersistentDataContainer().has(poolKey)) {
            return;
        }

        Player player = event.getPlayer();
        int rodChance = utilities.getPlayerRodChance(player);
        String poolMobId = mob.getPersistentDataContainer().get(poolKey, PersistentDataType.STRING);
        PoolMob poolMob = utilities.searchPoolMob(poolMobId);

        Random random = new Random();
        if(random.nextInt(100) + 1 < rodChance + poolMob.getCatchChance()) {
            utilities.giveReward(player, poolMob.getRandomRarity());
            String region = mob.getPersistentDataContainer().get(regionKey, PersistentDataType.STRING);
            PoolMob.spawnedMobs.put(region, PoolMob.spawnedMobs.get(region) - 1);
            mob.remove();
        } else {
            String failMessage = utilities.getRandomFailMessage();
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(failMessage));
            player.playSound(player.getLocation(), LastHunters.config.catchFailSound, 1.0F, 1.0F);
        }
    }
}
