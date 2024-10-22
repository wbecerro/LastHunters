package wbe.lastHunters.config.locations;

import org.bukkit.Location;
import wbe.lastHunters.LastHunters;

public class GolemSpot {

    private String id;

    private int position;

    private Location location;

    public static int spawnedGolems = 0;

    public GolemSpot(String id, int position, Location location) {
        this.id = id;
        this.position = position;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public GolemSpot getNext() {
        int newPosition = position + 1;
        if(newPosition > LastHunters.config.maxGolemSpots) {
            newPosition = 1;
        }
        return LastHunters.config.golemSpots.get(newPosition);
    }
}
