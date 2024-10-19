package wbe.lastHunters.config.locations;

import org.bukkit.Location;

public class CatalystSpot {

    private String id;

    private Location location;

    public static int catalystPlaced = 0;

    public CatalystSpot(String id, Location location) {
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

    public boolean isHeadPlaceable(Location location, String otherId) {
        if(!id.equalsIgnoreCase(otherId)) {
            return false;
        }

        if(this.location.getX() == location.getX() && this.location.getY() == location.getY() &&
                this.location.getY() == location.getY()) {
            return true;
        }

        return false;
    }
}
