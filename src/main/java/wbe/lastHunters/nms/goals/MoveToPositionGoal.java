package wbe.lastHunters.nms.goals;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import org.bukkit.Location;
import wbe.lastHunters.LastHunters;
import wbe.lastHunters.config.locations.GolemSpot;

public class MoveToPositionGoal extends Goal {
    PathfinderMob mob;
    GolemSpot spot;

    public MoveToPositionGoal(PathfinderMob mob, GolemSpot spot) {
        this.mob = mob;
        this.spot = spot;
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public void tick() {
        Location location = spot.getLocation();
        mob.getNavigation().moveTo(location.getX(), location.getY(), location.getZ(), LastHunters.config.golemSpeed);
        if(isInRange()) {
            spot = spot.getNext();
        }
    }

    private boolean isInRange() {
        Location location = spot.getLocation();
        for(int i=-1;i<2;i++) {
            for(int j=-1;j<2;j++) {
                if(Math.floor(mob.getX() + i) == location.getX() && Math.floor(mob.getZ() + j) == location.getZ()) {
                    return true;
                }
            }
        }

        return false;
    }
}
