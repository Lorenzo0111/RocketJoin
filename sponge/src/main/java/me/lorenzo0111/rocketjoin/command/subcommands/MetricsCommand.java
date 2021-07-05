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

package me.lorenzo0111.rocketjoin.command.subcommands;

import me.lorenzo0111.rocketjoin.command.RocketJoinSpongeCommand;
import me.lorenzo0111.rocketjoin.command.SubCommand;
import me.lorenzo0111.rocketjoin.common.ChatUtils;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class MetricsCommand extends SubCommand {

    public MetricsCommand(RocketJoinSpongeCommand command) {
        super(command);
    }

    @Override
    public String getName() {
        return "metrics";
    }

    @Override
    public void perform(CommandSource sender, CommandContext args) {

        final Optional<String> metrics = args.getOne("metrics");

        if (!metrics.isPresent()) {
            sender.sendMessage(Text.of(ChatUtils.colorize(this.getCommand().getPlugin().getConfig().prefix() + "&r &cTry to use /rj metrics allow/deny")));
            return;
        }

        if (metrics.get().equalsIgnoreCase("allow")) {
            sender.sendMessage(Text.of(ChatUtils.colorize("&8[&eMetrics&8] &7Thanks for &e&nallowing metrics&7.")));
            this.getCommand().getPlugin().editMetrics(sender,true);
            return;
        }

        if (metrics.get().equalsIgnoreCase("deny")) {
            sender.sendMessage(Text.of(ChatUtils.colorize("&8[&eMetrics&8] &7Ok, if you change your mind run &e&n/rj metrics allow&7.")));
            this.getCommand().getPlugin().editMetrics(sender,false);
            return;
        }

        sender.sendMessage(Text.of(ChatUtils.colorize(this.getCommand().getPlugin().getConfig().prefix() + "&r &cTry to use /rj metrics allow/deny")));

    }
}
