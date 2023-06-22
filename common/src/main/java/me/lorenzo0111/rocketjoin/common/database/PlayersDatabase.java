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

package me.lorenzo0111.rocketjoin.common.database;

import me.lorenzo0111.rocketjoin.common.exception.LoadException;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class PlayersDatabase {
    private static File file;
    private static ConfigurationNode data;
    private static YamlConfigurationLoader loader;
    private static List<String> users;

    public static void init(File folder) throws LoadException {
        file = new File(folder, "data.yml");
        try {
            if (!file.exists() && !file.createNewFile()) {
                throw new LoadException("Unable to create the data file");
            }
        } catch (IOException e) {
            throw new LoadException("Unable to create the data file");
        }

        try {
            loader = YamlConfigurationLoader.builder()
                    .file(file)
                    .build();

            data = loader.load();
            load();
        } catch (ConfigurateException e) {
            throw new LoadException("Unable to load the data file: " + e.getMessage());
        }
    }

    public static void add(@NotNull UUID user) {
        validate();

        if (users.contains(user.toString())) return;

        users.add(user.toString());
        save();
    }

    public static boolean contains(@NotNull UUID user) {
        validate();

        return users != null && users.contains(user.toString());
    }

    public static void load() {
        validate();

        if (data.node("data").virtual()) {
            users = new ArrayList<>();
            return;
        }

        try {
            users = data.node("data").getList(String.class);
        } catch (SerializationException ignored) {
            users = new ArrayList<>();
        }
    }

    public static void save() {
        try {
            data.node("data").setList(String.class, users);
            loader.save(data);
        } catch (ConfigurateException e) {
            System.out.println("An error has occurred while saving data: " + e.getMessage());
        }
    }

    private static void validate() {
        if (file == null || data == null) throw new IllegalStateException("Not initialized");
    }
}
