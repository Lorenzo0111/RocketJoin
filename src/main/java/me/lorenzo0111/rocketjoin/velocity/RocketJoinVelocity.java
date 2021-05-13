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

package me.lorenzo0111.rocketjoin.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import me.lorenzo0111.rocketjoin.common.ConfigExtractor;
import me.lorenzo0111.rocketjoin.velocity.command.RocketJoinVelocityCommand;
import me.lorenzo0111.rocketjoin.velocity.exception.LoadException;
import me.lorenzo0111.rocketjoin.velocity.listener.JoinListener;
import me.lorenzo0111.rocketjoin.velocity.listener.LeaveListener;
import me.lorenzo0111.rocketjoin.velocity.utilities.UpdateChecker;
import org.bstats.charts.SimplePie;
import org.bstats.velocity.Metrics;
import org.slf4j.Logger;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

@Plugin(id = "rocketjoin", name = "RocketJoin", version = "1.9.1",
        description = "Custom Join Messages Plugin", authors = {"Lorenzo0111"})
public class RocketJoinVelocity {
    private final ProxyServer server;
    private final Logger logger;
    private PluginContainer plugin;
    private UpdateChecker updater;
    private ConfigurationNode conf = null;
    private final Path path;
    private final Metrics.Factory metricsFactory;
    private File config;

    @Inject
    public RocketJoinVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory, Metrics.Factory factory) {
        this.server = server;
        this.logger = logger;
        this.path = dataDirectory;
        this.metricsFactory = factory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        final Optional<PluginContainer> pluginContainer = server.getPluginManager().fromInstance(this);

        if (!pluginContainer.isPresent()) {
            throw new LoadException("Unable to get plugin container. Report code: CONTAINER");
        }

        this.plugin = pluginContainer.get();

        this.updater = new UpdateChecker(this,82520, "https://bit.ly/RocketJoin");
        this.updater.fetch().thenAccept((available) -> this.updater.sendUpdateCheck(this.server.getConsoleCommandSource(),available));

        this.config = new ConfigExtractor(this.getClass(),path.toFile(),"config.yml")
                .extract();

        Objects.requireNonNull(config);

        this.reloadConfig();

        server.getEventManager().register(this, new JoinListener(this));
        server.getEventManager().register(this, new LeaveListener(this));

        Metrics metrics = metricsFactory.make(this, 11318);
        metrics.addCustomChart(new SimplePie("vip_features", () -> this.getConfig().node("enable_vip_features").getBoolean() ? "Yes" : "No"));

        CommandMeta meta = server.getCommandManager()
                .metaBuilder("rocketjoinvelocity")
                .aliases("rjv")
                .build();

        server.getCommandManager().register(meta,new RocketJoinVelocityCommand(this));

        logger.info("RocketJoin loaded!");
    }

    public Logger getLogger() {
        return logger;
    }

    public ProxyServer getServer() {
        return server;
    }

    public PluginContainer getPlugin() {
        return plugin;
    }

    public UpdateChecker getUpdater() {
        return updater;
    }

    public ConfigurationNode getConfig() {
        return conf;
    }

    public String getVersion() {
        final Optional<String> s = this.getPlugin().getDescription().getVersion();
        if (!s.isPresent()) {
            throw new LoadException("Version cannot be null.");
        }
        return s.get();
    }

    public void reloadConfig() {
        final YamlConfigurationLoader loader = YamlConfigurationLoader.builder().path(config.toPath()).build();
        try {
            this.conf = loader.load();
        } catch (ConfigurateException e) {
            logger.error("Unable to load config: ", e);
        }
    }
}