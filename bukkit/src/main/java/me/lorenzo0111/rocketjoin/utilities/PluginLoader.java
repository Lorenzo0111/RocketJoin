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

import me.lorenzo0111.pluginslib.command.Customization;
import me.lorenzo0111.pluginslib.updater.UpdateChecker;
import me.lorenzo0111.rocketjoin.RocketJoinBukkit;
import me.lorenzo0111.rocketjoin.command.RocketJoinCommand;
import me.lorenzo0111.rocketjoin.common.ChatUtils;
import me.lorenzo0111.rocketjoin.listener.JoinListener;
import me.lorenzo0111.rocketjoin.listener.LeaveListener;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;

public class PluginLoader {

    private final RocketJoinBukkit plugin;
    private final UpdateChecker updateChecker;
    private boolean placeholderapi = true;

    public PluginLoader(RocketJoinBukkit plugin) {
        this.plugin = plugin;
        this.updateChecker = plugin.getUpdater();
    }

    public void loadMetrics() {
        Metrics metrics = new Metrics(plugin, 9382);
        metrics.addCustomChart(new SimplePie("conditions", () -> String.valueOf(plugin.getConfiguration().conditions().childrenList().size())));
    }

    public void registerEvents() {
        Bukkit.getServer().getPluginManager().registerEvents(new LeaveListener(plugin), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new JoinListener(plugin,this), plugin);

        Customization customization = new Customization(
                ChatUtils.serializer().serialize(plugin.parse(plugin.getPrefix() + "&r &7Running &e" + plugin.getDescription().getName() + " &ev" + plugin.getDescription().getVersion() + " &7by &eLorenzo0111&7!")),
                ChatUtils.serializer().serialize(plugin.parse(plugin.getPrefix() + "&r &7Command not found, use &8/rocketjoin help&7 for a command list")),
                ChatUtils.serializer().serialize(plugin.parse(plugin.getPrefix() + "&r &7Use &8/rocketjoin help&7 for a command list"))
        );
        new RocketJoinCommand(plugin,"rocketjoin",customization);
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
        plugin.getLogger().info("Could not find PlaceholderAPI! Whitout PlaceholderAPI you can't use placeholders.");
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
