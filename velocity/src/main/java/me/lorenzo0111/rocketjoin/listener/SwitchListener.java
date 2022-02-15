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
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import me.lorenzo0111.rocketjoin.RocketJoinVelocity;

public class SwitchListener {
    private final RocketJoinVelocity plugin;

    public SwitchListener(RocketJoinVelocity plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onSwitch(ServerConnectedEvent event) {
        if (event.getPreviousServer().isEmpty()) return;

        if (plugin.getConfig().hide() && event.getPlayer().hasPermission(plugin.getConfig().hidePermission())) {
            return;
        }

        if (plugin.getConfig().serverSwitch().enabled()) {
            plugin.getServer().getScheduler().buildTask(plugin, () -> {
                for (Player audience : event.getPreviousServer().get().getPlayersConnected()) {
                    audience.sendMessage(plugin.parse(plugin.getConfig().serverSwitch().messageFrom()
                            .replace("{oldServer}", event.getPreviousServer().get().getServerInfo().getName())
                            .replace("{newServer}", event.getServer().getServerInfo().getName()), event.getPlayer()));
                }
                for (Player audience : event.getServer().getPlayersConnected()) {
                    audience.sendMessage(plugin.parse(plugin.getConfig().serverSwitch().messageTo()
                            .replace("{oldServer}", event.getPreviousServer().get().getServerInfo().getName())
                            .replace("{newServer}", event.getServer().getServerInfo().getName()), event.getPlayer()));
                }
            }).schedule();
        }
    }
}
