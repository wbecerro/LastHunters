package wbe.lastHunters.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import wbe.lastHunters.LastHunters;
import wbe.lastHunters.util.Utilities;

public class EntityShootBowListeners implements Listener {

    private LastHunters plugin = LastHunters.getInstance();

    private Utilities utilities = new Utilities();

    @EventHandler(priority = EventPriority.NORMAL)
    public void specialBowShoot(EntityShootBowEvent event) {
        
    }
}
