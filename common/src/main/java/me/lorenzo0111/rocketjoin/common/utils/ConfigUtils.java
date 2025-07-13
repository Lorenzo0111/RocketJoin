package me.lorenzo0111.rocketjoin.common.utils;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.List;
import java.util.Random;

public final class ConfigUtils {
    private static final Random RANDOM = new Random();

    public static String getRandomNode(ConfigurationNode node) {
        if (node.isList()) {
            try {
                List<String> list = node.getList(String.class);
                if (list == null || list.isEmpty()) return "";

                int randomIndex = RANDOM.nextInt(list.size());
                return list.get(randomIndex);
            } catch (SerializationException e) {
                e.printStackTrace();
            }

            return "";
        }

        return node.getString();
    }
}
