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

import me.lorenzo0111.rocketjoin.RocketJoinBungee;
import me.lorenzo0111.rocketjoin.updater.UpdateChecker;
import me.lorenzo0111.rocketjoin.utilities.PluginLoader;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.spongepowered.configurate.serialize.SerializationException;

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
            updateChecker.sendUpdateCheck(p);
        }

        String welcome = plugin.getConfiguration().welcome();
        if (!welcome.equalsIgnoreCase("disable")) {
            p.sendMessage(new TextComponent(plugin.parse(welcome, p)));
        }

        if (plugin.getConfiguration().hide() && p.hasPermission(plugin.getConfiguration().hidePermission()))
            return;

        String condition = plugin.getHandler().getCondition(p);

        try {
            this.executeCommands(condition, e.getPlayer());
        } catch (SerializationException serializationException) {
            serializationException.printStackTrace();
        }

        if (condition == null) {
            boolean join = plugin.getConfiguration().join().enabled();
            String message = plugin.getConfiguration().join().message();
            if (join) {
                plugin.getProxy().broadcast(plugin.parse(message,p));
            }
            if (plugin.getConfiguration().join().enableTitle()) {
                Title title = plugin.getProxy().createTitle()
                        .title(plugin.parse(plugin.getConfiguration().join().title(), p))
                        .subTitle(plugin.parse(plugin.getConfiguration().join().subTitle(), p))
                        .fadeIn(15)
                        .stay(40)
                        .fadeOut(15);
                p.sendTitle(title);
            }
            return;
        }

        plugin.getProxy().broadcast(plugin.parse(plugin.getConfiguration().join(condition),p));
    }

    private void executeCommands(String condition, ProxiedPlayer player) throws SerializationException {
        List<String> commands = condition == null ? plugin.getConfiguration().commands() : plugin.getConfiguration().commands(condition);

        for (String command : commands) {
            plugin.getProxy().getPluginManager().dispatchCommand(plugin.getProxy().getConsole(), command.replace("{player}", player.getName()));
        }
    }

}
