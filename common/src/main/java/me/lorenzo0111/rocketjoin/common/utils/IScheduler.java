package me.lorenzo0111.rocketjoin.common.utils;

/**
 * Simple workaround for some build problems.
 */
public interface IScheduler extends me.lorenzo0111.pluginslib.scheduler.IScheduler {
    @Override void async(Runnable runnable);
    @Override void sync(Runnable runnable);
}
