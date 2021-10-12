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

package me.lorenzo0111.rocketjoin.common.config.file;

import me.lorenzo0111.rocketjoin.common.ChatUtils;
import me.lorenzo0111.rocketjoin.common.config.ConditionConfiguration;
import me.lorenzo0111.rocketjoin.common.config.IConfiguration;
import me.lorenzo0111.rocketjoin.common.config.SingleConfiguration;
import me.lorenzo0111.rocketjoin.common.exception.LoadException;
import me.lorenzo0111.rocketjoin.common.platform.Platform;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class FileConfiguration implements IConfiguration {
    private static final Platform platform = Platform.getPlatform();
    private Map<String,Object> cache;
    private ConfigurationNode config;
    private final File file;

    public FileConfiguration(File file) {
        this.file = file;
        this.reload();
        this.migrate();
    }

    @Override
    public String version() {
        return config.node("config-version").getString();
    }

    @Override
    public String prefix() {
        return ChatUtils.colorize(config.node("prefix").getString());
    }

    @Override
    public String noPermission() {
        return ChatUtils.colorize(config.node("no_permission").getString());
    }

    @Override
    public boolean update() {
        return config.node("update-message").getBoolean();
    }

    @Override
    public SingleConfiguration join() {
        return get("join", () -> new FileSingleConfiguration(config.node("join")));
    }

    @Override
    public SingleConfiguration leave() {
        return get("leave", () -> new FileSingleConfiguration(config.node("leave")));
    }

    @Override
    public SingleConfiguration serverSwitch() {
        return get("serverSwitch", () -> new FileSingleConfiguration(config.node("serverSwitch")));
    }

    @Override
    public List<String> commands() throws SerializationException {
        return config.node("commands").getList(String.class, new ArrayList<>());
    }

    @Override
    public String join(String conditionKey) {
        if (conditionKey == null) {
            return this.join().message();
        }

        return ChatUtils.colorize(config.node("conditions",conditionKey,"join").getString());
    }

    @Override
    public String leave(String conditionKey) {
        if (conditionKey == null) {
            return this.leave().message();
        }

        return ChatUtils.colorize(config.node("conditions",conditionKey,"leave").getString());
    }

    @Override
    public List<String> commands(String conditionKey) {
        try {
            return config.node("conditions",conditionKey,"commands").getList(String.class,new ArrayList<>());
        } catch (SerializationException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public ConditionConfiguration condition(String conditionKey) {
        return get("c-" + conditionKey,() -> new FileCondition(config.node("conditions",conditionKey)));
    }

    @Override
    public ConfigurationNode conditions() {
        return config.node("conditions");
    }

    @Override
    public boolean hide() {
        return config.node("enable-hide").getBoolean();
    }

    @Override
    public String hidePermission() {
        return config.node("hide-permission").getString();
    }

    @Override
    public String welcome() {
        return ChatUtils.colorize(config.node("welcome").getString("disable"));
    }

    @Override
    public void reload() {
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder().path(file.toPath()).build();
        try {
            this.config = loader.load();
            this.cache = new HashMap<>();
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void migrate() {
        // 2.1.4
        if (platform.isProxy()) {
            ConfigurationNode serverSwitch = config.node("serverSwitch");

            if (serverSwitch.isNull() || serverSwitch.empty() || serverSwitch.virtual()) {
                try {
                    serverSwitch.node("enabled").set(false);
                    serverSwitch.node("message").set("&a{player} &7switched to &a{newServer}");
                } catch (SerializationException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @SuppressWarnings("unchecked")
    private <T> T get(String key, Callable<T> def) {
        if (cache.containsKey(key)) {
             return (T) cache.get(key);
        }

        try {
            T call = def.call();

            cache.put(key,call);
            return call;
        } catch (Exception e) {
            throw new LoadException("Unable to get configuration. Please report it to the github repository.");
        }
    }
}
