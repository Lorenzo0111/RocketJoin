/*
 *  This file is part of RocketJoin, licensed under the MIT License.
 *
 *  Copyright (c) Lorenzo0111
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.lorenzo0111.rocketjoin.utilities;

import me.lorenzo0111.rocketjoin.CustomJoinMessage;
import me.lorenzo0111.rocketjoin.command.MainCommand;
import me.lorenzo0111.rocketjoin.listener.Join;
import me.lorenzo0111.rocketjoin.listener.Leave;
import me.lorenzo0111.rocketjoin.updater.UpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;

public class PluginLoader {

    private final CustomJoinMessage plugin;
    private boolean placeholderapi = true;
    private UpdateChecker updateChecker;

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

    public void loadUpdater() {
        this.updateChecker = new UpdateChecker(plugin, 82520);
        this.updateChecker.updateCheck();
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

    public UpdateChecker getUpdater() {
        return updateChecker;
    }

    public boolean isPlaceholderapi() {
        return placeholderapi;
    }
}
