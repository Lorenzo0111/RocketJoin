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

package me.lorenzo0111.rocketjoin.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.proxy.Player;
import me.lorenzo0111.rocketjoin.velocity.RocketJoinVelocity;
import me.lorenzo0111.rocketjoin.velocity.utilities.ChatUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

import java.util.Objects;

public class LeaveListener {
    private final RocketJoinVelocity plugin;

    public LeaveListener(RocketJoinVelocity plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onQuit(DisconnectEvent e) {
        Player p = e.getPlayer();

        if (e.getPlayer().hasPermission("rocketjoin.vip") && plugin.getConfig().node("enable_vip_features").getBoolean() && plugin.getConfig().node("vip_leave").getBoolean()) {
            Component quitText = Component.text(ChatUtils.colorize(Objects.requireNonNull(plugin.getConfig().node("vip_leave_message").getString()).replace("{player}", p.getUsername())));

            plugin.getServer().getScheduler().buildTask(plugin, () -> {
                for (Audience audience : plugin.getServer().getAllPlayers()) {
                    audience.sendMessage(quitText);
                }
            }).schedule();

            return;
        }

        if (plugin.getConfig().node("enable_leave_message").getBoolean()) {
            Component quitText = Component.text(ChatUtils.colorize(Objects.requireNonNull(plugin.getConfig().node("leave_message").getString()).replace("{player}", p.getUsername())));

            plugin.getServer().getScheduler().buildTask(plugin, () -> {
                for (Audience audience : plugin.getServer().getAllPlayers()) {
                    audience.sendMessage(quitText);
                }
            }).schedule();
        }
    }

}
