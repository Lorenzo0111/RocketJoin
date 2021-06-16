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

package me.lorenzo0111.rocketjoin.bungeecord.utilities;

import me.lorenzo0111.rocketjoin.bungeecord.RocketJoinBungee;
import me.lorenzo0111.rocketjoin.bungeecord.command.RocketJoinBungeeCommand;
import me.lorenzo0111.rocketjoin.bungeecord.listener.JoinListener;
import me.lorenzo0111.rocketjoin.bungeecord.listener.LeaveListener;
import me.lorenzo0111.rocketjoin.bungeecord.updater.UpdateChecker;
import org.bstats.bungeecord.Metrics;
import org.bstats.charts.SimplePie;

public class PluginLoader {

    private final RocketJoinBungee plugin;
    private UpdateChecker updateChecker;

    public PluginLoader(RocketJoinBungee plugin) {
        this.plugin = plugin;
    }

    public void loadMetrics() {
        Metrics metrics = new Metrics(plugin, 10698);
        metrics.addCustomChart(new SimplePie("vip_features", () -> plugin.getConfiguration().node("enable_vip_features").getBoolean() ? "Yes" : "No"));
    }

    public void loadUpdater() {
        this.updateChecker = new UpdateChecker(plugin, 82520, "https://bit.ly/RocketJoin");
        this.updateChecker.sendUpdateCheck(plugin.getProxy().getConsole());
    }

    public void registerEvents() {
        plugin.getProxy().getPluginManager().registerListener(plugin,new LeaveListener(plugin));
        plugin.getProxy().getPluginManager().registerListener(plugin,new JoinListener(plugin,this));

        plugin.getProxy().getPluginManager().registerCommand(plugin, new RocketJoinBungeeCommand(plugin,this.getUpdater()));
    }

    public void placeholderHook() {
        plugin.getLogger().warning("PlaceholdersAPI is not supported on bungeecord.");
        plugin.getLogger().info("Loaded internal placeholers: {Player} and {DisplayPlayer}");
        plugin.getLogger().info(plugin.getDescription().getName() + " v" + plugin.getDescription().getVersion() + " by Lorenzo0111 is now enabled!");
    }

    public UpdateChecker getUpdater() {
        return updateChecker;
    }
}
