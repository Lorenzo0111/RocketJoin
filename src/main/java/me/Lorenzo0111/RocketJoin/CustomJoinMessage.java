package me.Lorenzo0111.RocketJoin;

import me.Lorenzo0111.RocketJoin.Updater.UpdateChecker;
import me.Lorenzo0111.RocketJoin.Utilities.PluginLoader;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomJoinMessage extends JavaPlugin implements Listener {

    /*

    Plugin by Lorenzo0111 - https://github.com/Lorenzo0111

     */

    public void onEnable() {

        // Load the plugin
        PluginLoader loader = new PluginLoader(this);
        loader.loadMetrics();
        loader.placeholderHook();
        loader.registerEvents();

        // Check for updates
        UpdateChecker checker = new UpdateChecker(this, 82520);
        checker.updateCheck();

        saveDefaultConfig();
    }

    public void onDisable() {
        getLogger().info("Plugin disabled!");
    }
}