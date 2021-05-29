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

package me.lorenzo0111.rocketjoin.sponge;

import com.google.inject.Inject;
import me.lorenzo0111.rocketjoin.common.ConfigExtractor;
import me.lorenzo0111.rocketjoin.sponge.command.RocketJoinSpongeCommand;
import me.lorenzo0111.rocketjoin.sponge.listener.JoinListener;
import me.lorenzo0111.rocketjoin.sponge.listener.LeaveListener;
import me.lorenzo0111.rocketjoin.sponge.utilities.UpdateChecker;
import me.lorenzo0111.rocketjoin.velocity.exception.LoadException;
import org.bstats.sponge.Metrics2;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.metric.MetricsConfigManager;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

@Plugin(id = "rocketjoin", name = "RocketJoin", version = "1.9.1.2",
        description = "Custom Join Messages Plugin", authors = {"Lorenzo0111"})
public class RocketJoinSponge {
    @Inject private Logger logger;
    @ConfigDir(sharedRoot = false) @Inject private Path path;
    private File config;
    private ConfigurationNode conf;
    @Inject private Game game;
    @Inject private MetricsConfigManager metricsConfigManager;
    @Inject private Metrics2.Factory factory;
    private PluginContainer plugin;
    private UpdateChecker updater;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        final Optional<PluginContainer> pluginContainer = game.getPluginManager().fromInstance(this);

        if (!pluginContainer.isPresent()) {
            throw new LoadException("Unable to get plugin container. Report code: CONTAINER");
        }

        this.plugin = pluginContainer.get();

        this.updater = new UpdateChecker(this,"rocketjoin", "https://bit.ly/RocketJoin");
        this.updater.fetch().thenAccept((available) -> this.updater.sendUpdateCheck(this.game.getServer().getConsole(),available));

        this.config = new ConfigExtractor(this.getClass(),path.toFile(),"config.yml")
                .extract();

        Objects.requireNonNull(config);

        this.reloadConfig();

        game.getEventManager().registerListeners(this, new JoinListener(this));
        game.getEventManager().registerListeners(this, new LeaveListener(this));

        CommandSpec myCommandSpec = CommandSpec.builder()
                .description(Text.of("RocketJoin main command"))
                .arguments(
                        GenericArguments.optional(GenericArguments.string(Text.of("subcommand"))),
                        GenericArguments.optional(GenericArguments.string(Text.of("metrics")))
                        )
                .executor(new RocketJoinSpongeCommand(this))
                .build();

        this.getGame().getCommandManager().register(plugin, myCommandSpec, "rj", "rocketjoin", "rjs");

        this.loadMetrics();

        logger.info("RocketJoin loaded!");
    }

    public void reloadConfig() {
        final YamlConfigurationLoader loader = YamlConfigurationLoader.builder().path(config.toPath()).build();
        try {
            this.conf = loader.load();
        } catch (ConfigurateException e) {
            logger.error("Unable to load config: ", e);
        }
    }

    public ConfigurationNode getConfig() {
        return conf;
    }

    public Game getGame() {
        return game;
    }

    public Logger getLogger() {
        return logger;
    }

    public PluginContainer getPlugin() {
        return plugin;
    }

    public UpdateChecker getUpdater() {
        return updater;
    }

    public String getVersion() {
        final Optional<String> s = this.getPlugin().getVersion();
        if (!s.isPresent()) {
            throw new LoadException("Version cannot be null.");
        }
        return s.get();
    }

    private void loadMetrics() {
        if (canMetrics()) {
            this.getLogger().info("Thanks for allowing metrics. Loading bStats..");
            Metrics2 metrics = factory.make(9382);
            metrics.addCustomChart(new Metrics2.SimplePie("vip_features", () -> this.getConfig().node("enable_vip_features").getBoolean() ? "Yes" : "No"));
        }
    }

    public void editMetrics(CommandSource source,boolean allow) {
        try {
            this.getConfig().node("already-asked").set(true);
        } catch (SerializationException e) {
            e.printStackTrace();
        }

        if (allow) {
            this.getGame().getCommandManager().process(source, String.format("sponge metrics %s enable", this.getPlugin().getId()));
            this.loadMetrics();
        }

    }

    public boolean canMetrics() {
        return this.metricsConfigManager.getCollectionState(this.plugin).asBoolean();
    }
}
