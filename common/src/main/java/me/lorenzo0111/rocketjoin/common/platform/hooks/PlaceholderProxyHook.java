package me.lorenzo0111.rocketjoin.common.platform.hooks;

import net.william278.papiproxybridge.api.PlaceholderAPI;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class PlaceholderProxyHook {
    private static PlaceholderAPI instance;

    public static CompletableFuture<String> replacePlaceholders(String message, UUID player) {
        if (instance == null) instance = PlaceholderAPI.createInstance();

        return instance.formatPlaceholders(message, player);
    }
}
