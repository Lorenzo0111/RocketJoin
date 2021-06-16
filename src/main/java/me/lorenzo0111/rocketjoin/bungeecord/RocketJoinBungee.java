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

package me.lorenzo0111.rocketjoin.bungeecord;

import me.lorenzo0111.rocketjoin.bungeecord.utilities.PluginLoader;
import me.lorenzo0111.rocketjoin.common.ConfigExtractor;
import me.lorenzo0111.rocketjoin.spigot.listener.JoinListener;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.util.logging.Level;

public class RocketJoinBungee extends Plugin {
    private ConfigurationNode configuration;
    private File config;

    @Override
    public void onEnable() {

        this.config = new ConfigExtractor(this.getClass(), this.getDataFolder(), "config.yml")
            .extract();

        PluginLoader loader = new PluginLoader(this);
        loader.loadUpdater();
        loader.loadMetrics();
        loader.registerEvents();
        loader.placeholderHook();
    }

    public ConfigurationNode getConfiguration() {
        return configuration;
    }

    public void reloadConfig() {
        final YamlConfigurationLoader loader = YamlConfigurationLoader.builder().path(config.toPath()).build();
        try {
            this.configuration = loader.load();
        } catch (ConfigurateException e) {
            this.getLogger().log(Level.SEVERE, "Unable to load config: ", e);
        }
    }

    public TextComponent parse(String string) {
        string = ChatColor.translateAlternateColorCodes('&', string);
        if (JoinListener.isCompatible()) {
            string = JoinListener.translateHexColorCodes(string);
        }
        return new TextComponent(string);
    }

    public String parse(String path, ProxiedPlayer player) {
        String str = this.getConfiguration().node(path).getString("").replace("{player}", player.getName()).replace("{DisplayPlayer}", player.getDisplayName());
        str = ChatColor.translateAlternateColorCodes('&', str);
        if (JoinListener.isCompatible()) {
            str = JoinListener.translateHexColorCodes(str);
        }
        return str;
    }

    public String getPrefix() {
        return this.getConfiguration().node("prefix").getString("");
    }
}
