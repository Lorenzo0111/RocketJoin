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

package me.lorenzo0111.rocketjoin.common.config;

import me.lorenzo0111.rocketjoin.common.ChatUtils;
import me.lorenzo0111.rocketjoin.common.IConfiguration;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileConfiguration implements IConfiguration {
    private ConfigurationNode config;
    private final File file;

    public FileConfiguration(File file) {
        this.file = file;
        this.reload();
    }

    @Override
    public ConfigurationNode get(Object... path) {
        return config.node(path);
    }

    @Override
    public <T> T property(Class<T> type, Object... path) throws SerializationException {
        return config.node(path).get(type);
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
    public ConfigurationNode join() {
        return config.node("join");
    }

    @Override
    public ConfigurationNode leave() {
        return config.node("leave");
    }

    @Override
    public ConfigurationNode firstJoin() {
        return config.node("first-join");
    }

    @Override
    public List<String> commands() throws SerializationException {
        return config.node("commands").getList(String.class, new ArrayList<>());
    }

    @Override
    public String join(String conditionKey) {
        return ChatUtils.colorize(config.node("conditions",conditionKey,"join").getString());
    }

    @Override
    public String leave(String conditionKey) {
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
    public ConfigurationNode condition(String conditionKey) {
        return config.node("conditions",conditionKey);
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
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }
    }
}
