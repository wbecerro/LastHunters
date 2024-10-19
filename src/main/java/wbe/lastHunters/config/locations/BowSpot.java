package wbe.lastHunters.config.locations;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BowSpot {

    private String id;

    private Location location;

    public BowSpot(String id, Location location) {
        this.id = id;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isPlayerHere(Player player) {
        Location location = player.getLocation();
        if(Math.floor(location.getX()) == Math.floor(location.getX()) &&
                Math.floor(location.getY()) == Math.floor(location.getY()) &&
                Math.floor(location.getZ()) == Math.floor(location.getZ())) {
            return true;
        }

        return false;
    }
}
