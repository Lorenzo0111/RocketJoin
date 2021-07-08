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

import me.lorenzo0111.rocketjoin.RocketJoinSponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

public class LeaveListener {
    private final RocketJoinSponge plugin;

    public LeaveListener(RocketJoinSponge plugin) {
        this.plugin = plugin;
    }

    @Listener
    public void onQuit(ClientConnectionEvent.Disconnect e) {
        Player p = e.getTargetEntity();

        if (plugin.getConfig().hide() && p.hasPermission(plugin.getConfig().hidePermission())) {
            e.setMessageCancelled(true);
            return;
        }

        String condition = plugin.getHandler().getCondition(p);
        if (condition == null && plugin.getConfig().leave().node("enabled").getBoolean()) {
            e.setMessage(plugin.parse(plugin.getConfig().leave().node("message").getString(""),e.getTargetEntity()));
            return;
        }

        e.setMessage(plugin.parse(plugin.getConfig().leave(condition), e.getTargetEntity()));

    }
}
