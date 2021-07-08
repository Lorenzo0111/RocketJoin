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

package me.lorenzo0111.rocketjoin.conditions;

import me.lorenzo0111.rocketjoin.RocketJoinBungee;
import me.lorenzo0111.rocketjoin.common.IConfiguration;
import me.lorenzo0111.rocketjoin.conditions.types.FirstCondition;
import me.lorenzo0111.rocketjoin.conditions.types.PermissionCondition;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConditionHandler {
    private final RocketJoinBungee plugin;
    private final List<Condition> conditions = new ArrayList<>();

    public ConditionHandler(RocketJoinBungee plugin) {
        this.plugin = plugin;
    }

    public void init() {
        this.conditions.clear();
        IConfiguration configuration = plugin.getConfiguration();
        ConfigurationNode conditions = configuration.conditions();

        for (ConfigurationNode node : conditions.childrenMap().values()) {
            String key = Objects.requireNonNull(node.key()).toString();

            switch (node.node("type").getString("null").toUpperCase()) {
                case "PERMISSION":
                    PermissionCondition permissionCondition = new PermissionCondition(key, node.node("value").getString("null"));
                    this.conditions.add(permissionCondition);
                    break;
                case "FIRST":
                    FirstCondition firstCondition = new FirstCondition(key);
                    this.conditions.add(firstCondition);
                    break;
                default:
                    plugin.getLogger().warning("Invalid condition type at '" + key + "'. Ignoring..");
                    break;
            }

        }
    }

    public @Nullable String getCondition(ProxiedPlayer player) {
        for (Condition condition : conditions) {
            if (condition.apply(player)) return condition.key();
        }

        return null;
    }
}
