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

import me.lorenzo0111.rocketjoin.common.config.ConditionConfiguration;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.List;

public class FileCondition implements ConditionConfiguration {
    private final ConfigurationNode configuration;

    public FileCondition(ConfigurationNode configuration) {
        this.configuration = configuration;
    }

    @Override
    public @NotNull String type() {
        return configuration.node("type").getString("");
    }

    @Override
    public @Nullable String value() {
        return configuration.node("value").getString();
    }

    @Override
    public String join() {
        return configuration.node("join").getString();
    }

    @Override
    public String leave() {
        return configuration.node("leave").getString();
    }

    @Override
    public boolean sound() {
        return configuration.node("sound").getBoolean();
    }

    @Override
    public Sound soundType() {
        return Sound.sound(Key.key(configuration.node("sound-type").getString("")), Sound.Source.AMBIENT, 60f,1f);
    }

    @Override
    public boolean fireworks() {
        return configuration.node("fireworks").getBoolean();
    }

    @Override
    public int fireworksAmount() {
        return configuration.node("fireworks-amount").getInt();
    }

    @Override
    public List<String> commands() {
        try {
            return configuration.node("commands").getList(String.class);
        } catch (SerializationException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }
}
