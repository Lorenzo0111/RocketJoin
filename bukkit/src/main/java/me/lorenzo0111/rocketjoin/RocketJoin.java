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

import me.clip.placeholderapi.PlaceholderAPI;
import me.lorenzo0111.pluginslib.audience.BukkitAudienceManager;
import me.lorenzo0111.rocketjoin.common.ChatUtils;
import me.lorenzo0111.rocketjoin.common.ConfigExtractor;
import me.lorenzo0111.rocketjoin.common.conditions.ConditionHandler;
import me.lorenzo0111.rocketjoin.common.config.IConfiguration;
import me.lorenzo0111.rocketjoin.common.config.file.FileConfiguration;
import me.lorenzo0111.rocketjoin.utilities.PluginLoader;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class RocketJoin extends JavaPlugin {
    private IConfiguration config;
    private PluginLoader loader;
    private ConditionHandler handler;

    @Override
    public void onEnable() {
        File file = new ConfigExtractor(this.getClass(),this.getDataFolder(), "config.yml")
                .extract();

        this.config = new FileConfiguration(file);
        this.handler = new ConditionHandler(config);

        this.reloadConfig();

        BukkitAudienceManager.init(this);

        // Load the plugin
        this.loader = new PluginLoader(this);
        this.loader.loadUpdater();
        this.loader.loadMetrics();
        this.loader.placeholderHook();
        this.loader.registerEvents();
    }

    public void onDisable() {
        BukkitAudienceManager.shutdown();
    }

    public PluginLoader getLoader() {
        return loader;
    }

    public IConfiguration getConfiguration() {
        return config;
    }

    @Override
    public void reloadConfig() {
        config.reload();
        handler.init();

        if (config.version() == null) {
            this.getLogger().severe("You are using an old configuration, consider deleting the config.yml and restarting the server. If you need help you can also join in our support server.");
        }
    }

    public Component parseComponent(String string) {
        return Component.text(parse(string));
    }

    public String parse(String string) {
        string = ChatUtils.colorize(string);
        return string;
    }

    public String parse(@Nullable String string, Player player) {
        if (string == null) return null;
        String str = string.replace("{player}", player.getName()).replace("{DisplayPlayer}", player.getDisplayName());
        if (loader.isPlaceholderapi()) {
            str = PlaceholderAPI.setPlaceholders(player,str);
        }
        str = this.parse(str);
        return str;
    }

    public static RocketJoin instance() {
        return RocketJoin.getPlugin(RocketJoin.class);
    }

    public ConditionHandler getHandler() {
        return handler;
    }

    public String getPrefix() {
        return this.getConfiguration().prefix();
    }
}