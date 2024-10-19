package wbe.lastHunters.hooks;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

public class WorldGuardManager {

    public static StateFlag poolMobsFlag = new StateFlag("lasthunters-pool", true);

    public static void loadFlags() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        StateFlag poolFlag = new StateFlag("lasthunters-pool", true);

        try {
            registry.register(poolFlag);
            poolMobsFlag = poolFlag;
        } catch(Exception ex) {
            Flag<?> existing = registry.get("lasthunters-pool");
            if(existing instanceof StateFlag) {
                poolMobsFlag = (StateFlag) existing;
            } else {
                throw new RuntimeException("Flag already exists, another plugin is conflicting.");
            }
        }
    }
}
