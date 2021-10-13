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

package me.lorenzo0111.rocketjoin.common.platform;

public enum Platform {
    BUKKIT(false),
    SPONGE(false),
    BUNGEECORD(true),
    VELOCITY(true);

    private final boolean proxy;

    Platform(boolean proxy) {
        this.proxy = proxy;
    }

    public boolean isProxy() {
        return proxy;
    }

    public static Platform getPlatform() {
        if (hasClass("org.bukkit.Bukkit")) return BUKKIT;
        if (hasClass("org.spongepowered.api.Game")) return SPONGE;
        if (hasClass("net.md_5.bungee.api.ProxyServer")) return BUNGEECORD;
        if (hasClass("com.velocitypowered.api.plugin.Plugin")) return VELOCITY;

        throw new IllegalStateException("Unable to find platform name");
    }

    private static boolean hasClass(String name) {
        try {
            Class.forName(name);
            return true;
        }catch (Exception ignored){}

        return false;
    }
}
