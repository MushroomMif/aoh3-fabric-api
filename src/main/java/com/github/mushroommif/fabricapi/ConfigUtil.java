package com.github.mushroommif.fabricapi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Supplier;

/**
 * A util to easily load mod configs from json files using Gson
 */
public final class ConfigUtil {
    public static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    /**
     * Load mod config from the json file or create a json file with default parameters
     * @param configClass Class of the config object
     * @param modId Id of the config's mod. It will be used as a name to config file
     * @param defaultConfigSupplier Supplier of the default config
     * @return Loaded or default config object
     * @param <T> Config object
     */
    public static <T> T loadOrCreateConfig(Class<T> configClass, String modId, Supplier<T> defaultConfigSupplier) {
        File configFile = FabricLoader.getInstance().getConfigDir()
                .resolve( modId + ".json")
                .toFile();
        T config;

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();

                config = defaultConfigSupplier.get();
                String defaultConfigJson = gson.toJson(config);

                try (FileWriter writer = new FileWriter(configFile)) {
                    writer.write(defaultConfigJson);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to create " + modId +  " config file", e);
            }
        } else {
            try (FileReader reader = new FileReader(configFile)) {
                config = gson.fromJson(reader, configClass);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read " + modId + " config file", e);
            }
        }

        return config;
    }

    private ConfigUtil() {}
}
