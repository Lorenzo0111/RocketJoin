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

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import me.lorenzo0111.rocketjoin.RocketJoinVelocity;
import me.lorenzo0111.rocketjoin.audience.WrappedPlayer;
import me.lorenzo0111.rocketjoin.common.database.PlayersDatabase;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.spongepowered.configurate.serialize.SerializationException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class JoinListener {
    private final RocketJoinVelocity plugin;

    public JoinListener(RocketJoinVelocity plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onJoin(ServerPostConnectEvent e) {
        if (e.getPreviousServer() != null) return;

        Player p = e.getPlayer();

        if (plugin.getConfig().update() && p.hasPermission("rocketjoin.update")) {
            plugin.getUpdater().sendUpdateCheck(p);
        }

        String welcome = plugin.getConfig().welcome();
        if (!welcome.equalsIgnoreCase("disable")) {
            p.sendMessage(plugin.parse(welcome,p));
        }

        if (plugin.getConfig().hide() && p.hasPermission(plugin.getConfig().hidePermission()))
            return;

        String condition = plugin.getHandler().getCondition(WrappedPlayer.wrap(p));

        try {
            this.executeCommands(condition, e.getPlayer());
        } catch (SerializationException serializationException) {
            serializationException.printStackTrace();
        }

        if (condition == null && e.getPlayer().getCurrentServer().isPresent()) {
            boolean join = plugin.getConfig().join().enabled();
            Component message = plugin.parse(plugin.getConfig().join().message(),p);
            Component otherServerMessage = plugin.getConfig().join().otherServerMessage().isEmpty() ? null :
                    plugin.parse(plugin.getConfig().join().otherServerMessage()
                            .replace("{server}", p.getCurrentServer().get().getServerInfo().getName()), p);
            ArrayList<RegisteredServer> otherServers = new ArrayList<>(plugin.getServer().getAllServers());
            otherServers.remove(p.getCurrentServer().get().getServer());
            if (join) {
                plugin.getServer().getScheduler().buildTask(plugin, () -> {
                    for (Audience audience : p.getCurrentServer().get().getServer().getPlayersConnected()) {
                        audience.sendMessage(message);
                    }
                    if (otherServerMessage != null) {
                        for (Audience audience : otherServers) {
                            audience.sendMessage(otherServerMessage);
                        }
                    }
                }).schedule();
            }

            if (plugin.getConfig().join().enableTitle()) {
                final Title.Times times = Title.Times.of(Ticks.duration(15), Duration.ofMillis(3000), Ticks.duration(20));
                final Title title = Title.title(
                        plugin.parse(plugin.getConfig().join().title(),p),
                        plugin.parse(plugin.getConfig().join().subTitle(),p),
                        times
                );

                p.showTitle(title);
            }
            return;
        }

        plugin.getServer().getScheduler().buildTask(plugin, () -> {
            for (Audience audience : plugin.getServer().getAllPlayers()) {
                audience.sendMessage(plugin.parse(plugin.getConfig().join(condition),p));
            }
        }).schedule();

        PlayersDatabase.add(p.getUniqueId());
    }

    private void executeCommands(String condition, Player player) throws SerializationException {
        List<String> commands = condition == null ? plugin.getConfig().commands() : plugin.getConfig().commands(condition);

        for (String command : commands) {
            plugin.getServer().getCommandManager().executeAsync(plugin.getServer().getConsoleCommandSource(), command.replace("{player}", player.getUsername()));
        }
    }

}
