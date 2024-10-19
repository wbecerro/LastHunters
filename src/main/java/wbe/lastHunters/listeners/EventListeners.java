package wbe.lastHunters.listeners;

import org.bukkit.plugin.PluginManager;
import wbe.lastHunters.LastHunters;

public class EventListeners {

    private LastHunters plugin = LastHunters.getInstance();

    public void initializeListeners(){
        PluginManager pluginManager = plugin.getServer().getPluginManager();

        pluginManager.registerEvents(new PlayerFishListeners(), plugin);
        pluginManager.registerEvents(new EntityDamageListeners(), plugin);
        pluginManager.registerEvents(new EntityTargetLivingEntityListeners(), plugin);
    }
}
