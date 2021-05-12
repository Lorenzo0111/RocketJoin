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

package me.lorenzo0111.rocketjoin.sponge.listener;

import me.lorenzo0111.rocketjoin.sponge.RocketJoinSponge;
import me.lorenzo0111.rocketjoin.velocity.utilities.ChatUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;

import java.util.Objects;

public class LeaveListener {
    private final RocketJoinSponge plugin;

    public LeaveListener(RocketJoinSponge plugin) {
        this.plugin = plugin;
    }

    @Listener
    public void onQuit(ClientConnectionEvent.Disconnect e) {
        Player p = e.getTargetEntity();

        if (p.hasPermission("rocketjoin.vip") && plugin.getConfig().node("enable_vip_features").getBoolean() && plugin.getConfig().node("vip_leave").getBoolean()) {
            Text quitText = Text.of(ChatUtils.colorize(Objects.requireNonNull(plugin.getConfig().node("vip_leave_message").getString()).replace("{player}", p.getName())));

            e.setMessage(quitText);

            return;
        }

        if (plugin.getConfig().node("enable_leave_message").getBoolean()) {
            Text quitText = Text.of(ChatUtils.colorize(Objects.requireNonNull(plugin.getConfig().node("leave_message").getString()).replace("{player}", p.getName())));

            e.setMessage(quitText);
            return;
        }

        e.setMessageCancelled(true);
    }
}
