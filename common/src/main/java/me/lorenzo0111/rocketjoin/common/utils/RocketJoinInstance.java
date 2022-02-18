package me.lorenzo0111.rocketjoin.common.utils;

import me.lorenzo0111.pluginslib.updater.UpdateChecker;

public final class RocketJoinInstance {
    private static UpdateChecker updater;

    public static UpdateChecker getUpdaterOrSet(InstanceSetter<UpdateChecker> setter) {
        if (updater == null) updater = setter.set();

        return updater;
    }

    public interface InstanceSetter<T> {
        T set();
    }
}
