package com.github.mushroommif.fabricapi.event;

import net.fabricmc.loader.api.ModContainer;

import java.util.function.Supplier;

public final class ResourceLoaderEvents {

    public static final FabricEvent<ShouldLoadModResources> SHOULD_LOAD_MOD_RESOURCES = FabricEvent.create(handlers -> mod -> {
        for (ShouldLoadModResources handler : handlers) {
            ShouldLoadModResources.Result result = handler.shouldLoadResources(mod);
            if (result != ShouldLoadModResources.Result.PASS) {
                return result;
            }
        }

        return ShouldLoadModResources.Result.PASS;
    });

    public static final FabricEvent<OnStoreResource> ON_STORE_RESOURCE = FabricEvent.create(handlers -> (provider, resourcePath, resourceData) -> {
        for (OnStoreResource handler : handlers) {
            if (!handler.onStoreResource(provider, resourcePath, resourceData)) {
                return false;
            }
        }

        return true;
    });

    public static final FabricEvent<AfterResourcesStore> AFTER_RESOURCES_STORE = FabricEvent.create(listeners -> () -> {
        for (AfterResourcesStore listener : listeners) {
            listener.afterResourcesStore();
        }
    });

    @FunctionalInterface
    public interface ShouldLoadModResources {
        /**
         * Called for each loaded mod to determine should are its resources should be processed or not
         * @param mod Mod to check
         * @return Result of the check. See {@link Result} values javadoc
         */
        Result shouldLoadResources(ModContainer mod);

        enum Result {
            /**
             * Load resources no matter what
             */
            SUCCESS,
            /**
             * Use default check if other mods are not against it
             */
            PASS,
            /**
             * Do not load resources
             */
            FAIL
        }
    }

    @FunctionalInterface
    public interface OnStoreResource {
        /**
         * Called on loading of each resource file from every (allowed to have the resource loading) mod
         * @param provider Mod from which the resource file came from
         * @param resourcePath Path to the ree resource file inside the mod jar
         * @param resourceDataSupplier Function to get data of the resource file. If loading of the data
         *                             is failed for some reason, it will log the error to game console
         *                             and return an empty array
         * @return Is file should be stored and provided to the {@link aoc.kingdoms.lukasz.jakowski.FileManager}
         * on request or not
         */
        boolean onStoreResource(ModContainer provider, String resourcePath, Supplier<byte[]> resourceDataSupplier);
    }

    @FunctionalInterface
    public interface AfterResourcesStore {
        /**
         * Called after all resources from all mods were processed by the resource loader
         */
        void afterResourcesStore();
    }

    private ResourceLoaderEvents() {}
}
