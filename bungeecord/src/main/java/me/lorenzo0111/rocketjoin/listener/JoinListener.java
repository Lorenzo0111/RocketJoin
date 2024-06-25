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

package me.lorenzo0111.rocketjoin.listener;

import me.lorenzo0111.pluginslib.updater.UpdateChecker;
import me.lorenzo0111.rocketjoin.RocketJoinBungee;
import me.lorenzo0111.rocketjoin.audience.WrappedPlayer;
import me.lorenzo0111.rocketjoin.common.database.PlayersDatabase;
import me.lorenzo0111.rocketjoin.utilities.BungeeUtilities;
import me.lorenzo0111.rocketjoin.utilities.PluginLoader;
import net.kyori.adventure.title.Title;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.time.Duration;
import java.util.List;

public class JoinListener implements Listener {
    private final RocketJoinBungee plugin;
    private final UpdateChecker updateChecker;

    public JoinListener(RocketJoinBungee plugin, PluginLoader loader) {
        this.plugin = plugin;
        this.updateChecker = loader.getUpdater();
    }

    @EventHandler
    public void onJoin(PostLoginEvent e) {
        ProxiedPlayer p = e.getPlayer();

        if (plugin.getConfiguration().update() && p.hasPermission("rocketjoin.update")) {
            updateChecker.sendUpdateCheck(plugin.getAudiences().player(p));
        }

        String welcome = plugin.getConfiguration().welcome();
        if (!welcome.equalsIgnoreCase("disable")) {
            plugin.sendMessage(p,plugin.parse(welcome, p));
        }

        if (plugin.getConfiguration().hide() && p.hasPermission(plugin.getConfiguration().hidePermission()))
            return;

        String condition = plugin.getHandler().getCondition(WrappedPlayer.wrap(p));

        try {
            this.executeCommands(condition, e.getPlayer());
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if (condition == null) {
            boolean join = plugin.getConfiguration().join().enabled();
            String message = plugin.getConfiguration().join().message();
            if (join) {
                BungeeUtilities.broadcast(plugin,message,p);
            }
            if (plugin.getConfiguration().join().enableTitle()) {
                Title title = Title.title(
                                plugin.parse(plugin.getConfiguration().join().title(), p),
                                plugin.parse(plugin.getConfiguration().join().subTitle(), p),
                                Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(2), Duration.ofMillis(500)));
                plugin.getAudiences().player(p).showTitle(title);
            }
            return;
        }

        BungeeUtilities.broadcast(plugin,plugin.getConfiguration().join(condition),p);
        PlayersDatabase.add(p.getUniqueId());
    }

    private void executeCommands(String condition, ProxiedPlayer player) throws Exception {
        List<String> commands = condition == null ? plugin.getConfiguration().commands() : plugin.getConfiguration().commands(condition);

        for (String command : commands) {
            plugin.getProxy().getPluginManager().dispatchCommand(plugin.getProxy().getConsole(), command.replace("{player}", player.getName()));
        }
    }

}
