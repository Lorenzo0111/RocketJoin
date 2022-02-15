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
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import me.lorenzo0111.rocketjoin.RocketJoinVelocity;
import me.lorenzo0111.rocketjoin.audience.WrappedPlayer;
import net.kyori.adventure.text.Component;


public class LeaveListener {
    private final RocketJoinVelocity plugin;

    public LeaveListener(RocketJoinVelocity plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onQuit(DisconnectEvent e) {
        Player p = e.getPlayer();

        if (plugin.getConfig().hide() && p.hasPermission(plugin.getConfig().hidePermission())) {
            return;
        }

        String condition = plugin.getHandler().getCondition(WrappedPlayer.wrap(p));
        if (condition == null) {
            if (e.getPlayer().getCurrentServer().isEmpty()) return;

            if (plugin.getConfig().leave().enabled())
                plugin.getServer().getScheduler().buildTask(plugin, () -> {
                    for (Player audience : e.getPlayer().getCurrentServer().get().getServer().getPlayersConnected()) {
                        audience.sendMessage(plugin.parse(plugin.getConfig().leave().message(),p));
                    }
                    for (Player audience : plugin.getServer().getAllPlayers()) {
                        audience.sendMessage(plugin.parse(plugin.getConfig().leave().otherServerMessage()
                                .replace("{server}", e.getPlayer().getCurrentServer().get().getServerInfo().getName()),p));
                    }
                }).schedule();
            return;
        }

        plugin.getServer().getScheduler().buildTask(plugin, () -> {
            for (Player audience : plugin.getServer().getAllPlayers()) {
                audience.sendMessage(plugin.parse(plugin.getConfig().leave(condition),p));
            }
        }).schedule();
    }

}
