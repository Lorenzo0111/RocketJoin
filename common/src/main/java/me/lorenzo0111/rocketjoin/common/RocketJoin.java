package me.lorenzo0111.rocketjoin.common;

import me.lorenzo0111.pluginslib.updater.UpdateChecker;
import me.lorenzo0111.rocketjoin.common.config.IConfiguration;
import me.lorenzo0111.rocketjoin.common.utils.IScheduler;
import me.lorenzo0111.rocketjoin.common.utils.RocketJoinInstance;

import java.util.UUID;

public interface RocketJoin {
    String getVersion();
    IScheduler getScheduler();
    IConfiguration getConfiguration();
    boolean isVanished(UUID player);
    default UpdateChecker getUpdater() {
        return RocketJoinInstance.getUpdaterOrSet(() -> new UpdateChecker(getScheduler(), getVersion(), "RocketJoin", 82520, "https://bit.ly/RocketJoin", null, null));
    }
}
