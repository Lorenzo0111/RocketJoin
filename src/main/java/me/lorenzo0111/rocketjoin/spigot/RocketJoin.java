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

package me.lorenzo0111.rocketjoin.spigot;

import me.clip.placeholderapi.PlaceholderAPI;
import me.lorenzo0111.rocketjoin.common.ConfigExtractor;
import me.lorenzo0111.rocketjoin.spigot.listener.JoinListener;
import me.lorenzo0111.rocketjoin.spigot.utilities.PluginLoader;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.util.Objects;
import java.util.logging.Level;

public class RocketJoin extends JavaPlugin {
    private ConfigurationNode config;
    private File configFile;
    private PluginLoader loader;

    public void onEnable() {

        // Load the plugin
        this.loader = new PluginLoader(this);
        this.loader.loadUpdater();
        this.loader.loadMetrics();
        this.loader.placeholderHook();
        this.loader.registerEvents();

        this.configFile = new ConfigExtractor(this.getClass(),this.getDataFolder(),"config.yml")
                .extract();
        this.reloadConfig();

        Objects.requireNonNull(configFile);


    }

    public void onDisable() {
        getLogger().info("Plugin disabled!");
    }

    public PluginLoader getLoader() {
        return loader;
    }

    public ConfigurationNode getConfiguration() {
        return config;
    }

    @Override
    public void reloadConfig() {
        final YamlConfigurationLoader loader = YamlConfigurationLoader.builder().path(configFile.toPath()).build();
        try {
            this.config = loader.load();
        } catch (ConfigurateException e) {
            this.getLogger().log(Level.SEVERE, "Unable to load config: ", e);
        }
    }

    public String parse(String string) {
        string = ChatColor.translateAlternateColorCodes('&', string);
        if (JoinListener.isCompatible()) {
            string = JoinListener.translateHexColorCodes(string);
        }
        return string;
    }

    public String parse(String path, Player player) {
        String str = this.getConfiguration().node(path).getString("").replace("{player}", player.getName()).replace("{DisplayPlayer}", player.getDisplayName());
        if (loader.isPlaceholderapi()) {
            str = PlaceholderAPI.setPlaceholders(player,str);
        }
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