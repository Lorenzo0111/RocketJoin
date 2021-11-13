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

package me.lorenzo0111.rocketjoin;

import me.lorenzo0111.rocketjoin.common.ChatUtils;
import me.lorenzo0111.rocketjoin.common.ConfigExtractor;
import me.lorenzo0111.rocketjoin.common.conditions.ConditionHandler;
import me.lorenzo0111.rocketjoin.common.config.IConfiguration;
import me.lorenzo0111.rocketjoin.common.config.file.FileConfiguration;
import me.lorenzo0111.rocketjoin.common.database.PlayersDatabase;
import me.lorenzo0111.rocketjoin.common.exception.LoadException;
import me.lorenzo0111.rocketjoin.common.hex.HexUtils;
import me.lorenzo0111.rocketjoin.utilities.PluginLoader;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class RocketJoinBungee extends Plugin {
    private IConfiguration configuration;
    private ConditionHandler handler;

    @Override
    public void onEnable() {
        File config = new ConfigExtractor(this.getClass(), this.getDataFolder(), "config.yml")
                .extract();

        try {
            PlayersDatabase.init(this.getDataFolder());
        } catch (LoadException e) {
            this.getLogger().severe(e.getMessage());
            return;
        }

        this.configuration = new FileConfiguration(config);
        try {
            this.handler = new ConditionHandler(configuration);
        } catch (LoadException e) {
            this.getLogger().severe(e.getMessage());
            return;
        }

        this.reloadConfig();

        PluginLoader loader = new PluginLoader(this);
        loader.loadUpdater();
        loader.loadMetrics();
        loader.registerEvents();
        loader.placeholderHook();
    }

    public IConfiguration getConfiguration() {
        return configuration;
    }

    public void reloadConfig() {
        this.configuration.reload();
        try {
            handler.init();
        } catch (LoadException e) {
            this.getLogger().severe(e.getMessage());
        }
    }

    public TextComponent parse(String string) {
        string = ChatColor.translateAlternateColorCodes('&', string);
        string = HexUtils.translateHexColorCodes(string);
        return new TextComponent(string);
    }

    public TextComponent parse(@Nullable String string, ProxiedPlayer player) {
        String str = string == null ? "" : string.replace("{player}", player.getName()).replace("{DisplayPlayer}", player.getDisplayName());
        str = ChatUtils.colorize(str);
        return new TextComponent(str);
    }

    public ConditionHandler getHandler() {
        return handler;
    }

    public String getPrefix() {
        return this.getConfiguration().prefix();
    }
}
