package wbe.lastHunters.config.locations;

import org.bukkit.Location;

public class ChickenCannon {

    private String id;

    private Location location;

    public ChickenCannon(String id, Location location) {
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

    public void spawnChicken() {

    }

    private String getChickenType() {
        return null;
    }
}
