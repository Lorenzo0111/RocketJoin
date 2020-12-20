package me.Lorenzo0111.RocketJoin.Utilities;

import me.Lorenzo0111.RocketJoin.Command.MainCommand;
import me.Lorenzo0111.RocketJoin.CustomJoinMessage;
import me.Lorenzo0111.RocketJoin.Listener.Join;
import me.Lorenzo0111.RocketJoin.Listener.Leave;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;

public class PluginLoader {

    private final CustomJoinMessage plugin;
    public boolean placeholderapi = true;

    public PluginLoader(CustomJoinMessage plugin) {
        this.plugin = plugin;
    }

    public void loadMetrics() {
        Metrics metrics = new Metrics(plugin, 9382);
        metrics.addCustomChart(new Metrics.SimplePie("vip_features", () -> {
            if (plugin.getConfig().getBoolean("enable_vip_features")) {
                return "Yes";
            }
            return "No";
        }));
    }

    public void registerEvents() {
        Bukkit.getServer().getPluginManager().registerEvents(new Leave(plugin,this), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new Join(plugin,this), plugin);

        plugin.getCommand("rocketjoin").setExecutor(new MainCommand(plugin));

        plugin.getCommand("rocketjoin").setTabCompleter(new MainCommand(plugin));
    }

    public void placeholderHook() {

        // Check if PlaceholderAPI is enabled
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            plugin.getLogger().info("PlaceholderAPI hooked!");
            placeholderapi = true;
            plugin.getLogger().info(plugin.getDescription().getName() + " v" + plugin.getDescription().getVersion() + " by Lorenzo0111 is now enabled!");
            return;
        }

        placeholderapi = false;
        plugin.getLogger().warning("Could not find PlaceholderAPI! Whitout PlaceholderAPI you can't use placeholders.");
        plugin.getLogger().info("Loaded internal placeholers: {Player} and {DisplayPlayer}");
        plugin.getLogger().info(plugin.getDescription().getName() + " v" + plugin.getDescription().getVersion() + " by Lorenzo0111 is now enabled!");
    }

}
