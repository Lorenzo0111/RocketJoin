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

import com.google.inject.Inject;
import me.lorenzo0111.rocketjoin.command.RocketJoinSpongeCommand;
import me.lorenzo0111.rocketjoin.common.ChatUtils;
import me.lorenzo0111.rocketjoin.common.ConfigExtractor;
import me.lorenzo0111.rocketjoin.common.RocketJoin;
import me.lorenzo0111.rocketjoin.common.conditions.ConditionHandler;
import me.lorenzo0111.rocketjoin.common.config.IConfiguration;
import me.lorenzo0111.rocketjoin.common.config.file.FileConfiguration;
import me.lorenzo0111.rocketjoin.common.exception.LoadException;
import me.lorenzo0111.rocketjoin.common.utils.IScheduler;
import me.lorenzo0111.rocketjoin.listener.JoinListener;
import me.lorenzo0111.rocketjoin.listener.LeaveListener;
import net.kyori.adventure.text.Component;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.bstats.sponge.Metrics;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

@Plugin("rocketjoin")
public class RocketJoinSponge implements RocketJoin {
    private IScheduler scheduler;
    @Inject private Logger logger;
    @ConfigDir(sharedRoot = false) @Inject private Path path;
    private IConfiguration conf;
    @Inject private Game game;
    private PluginContainer plugin;
    private ConditionHandler handler;

    @Inject
    public RocketJoinSponge(Metrics.@NotNull Factory factory) {
        factory.make(13863);
    }

    @Listener
    public void onServerStart(ConstructPluginEvent event) {
        final Optional<PluginContainer> pluginContainer = game.pluginManager().fromInstance(this);

        if (!pluginContainer.isPresent()) {
            try {
                throw new LoadException("Unable to get plugin container. Report code: CONTAINER");
            } catch (LoadException e) {
                this.getLogger().error(e.getMessage());
            }
            return;
        }

        this.plugin = pluginContainer.get();
        this.scheduler = new IScheduler() {
            @Override
            public void async(Runnable runnable) {
                Sponge.game()
                        .asyncScheduler()
                        .executor(plugin)
                        .execute(runnable);
            }

            @Override
            public void sync(Runnable runnable) {
                runnable.run();
            }
        };

        File config = new ConfigExtractor(this.getClass(),path.toFile(), "config.yml")
                .extract();

        this.conf = new FileConfiguration(config);
        try {
            this.handler = new ConditionHandler(conf);
        } catch (LoadException e) {
            this.getLogger().error(e.getMessage());
            return;
        }

        this.reloadConfig();

        game.eventManager().registerListeners(plugin, new JoinListener(this));
        game.eventManager().registerListeners(plugin, new LeaveListener(this));



        logger.info("RocketJoin loaded!");
    }

    @Listener
    public void registerCommands(@NotNull RegisterCommandEvent<Command.Parameterized> event) {
        Parameter.Value<String> subcommand = Parameter.string()
                .key("subcommand")
                .optional()
                .build();

        Command.Parameterized command = Command.builder()
                .extendedDescription(Component.text("RocketJoin main command"))
                .executor(new RocketJoinSpongeCommand(this, subcommand))
                .addParameter(subcommand)
                .build();

        event.register(plugin,command,"rj", "rjs", "rocketjoin");
    }

    public void reloadConfig() {
        conf.reload();
        try {
            handler.init();
        } catch (LoadException e) {
            this.getLogger().error(e.getMessage());
        }
    }

    public IConfiguration getConfig() {
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

    public String getVersion() {
        final ArtifactVersion s = this.getPlugin().metadata().version();

        return s.getMajorVersion() + "." + s.getMinorVersion();
    }

    @Override
    public IScheduler getScheduler() {
        return scheduler;
    }

    @Override
    public IConfiguration getConfiguration() {
        return conf;
    }

    @Override
    public boolean isVanished(UUID player) {
        return false;
    }

    public Component parse(@NotNull String string, @NotNull Player source) {
        String str = string.replace("{player}", source.name());

        return ChatUtils.colorize(str);
    }

    public ConditionHandler getHandler() {
        return handler;
    }
}
