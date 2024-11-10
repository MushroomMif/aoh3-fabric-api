package com.github.mushroommif.fabricapi.event;

public final class FileLoadingEvents {

    /**
     * This is the proper place to load your images into {@link aoc.kingdoms.lukasz.textures.ImageManager}
     */
    public static final FabricEvent<Images> IMAGES = FabricEvent.create(listeners -> () -> {
        for (Images listener : listeners) {
            listener.onImagesLoading();
        }
    });

    /**
     * This is the proper place to load all your custom files
     */
    public static final FabricEvent<End> END = FabricEvent.create(listeners -> () -> {
        for (End listener : listeners) {
            listener.onEndLoading();
        }
    });

    @FunctionalInterface
    public interface Images {
        /**
         * Called right after the game has loaded all vanilla images
         */
        void onImagesLoading();
    }

    @FunctionalInterface
    public interface End {
        /**
         * Called after all vanilla files were loaded
         */
        void onEndLoading();
    }

    private FileLoadingEvents() {}
}
