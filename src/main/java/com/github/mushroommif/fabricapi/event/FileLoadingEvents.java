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

    @FunctionalInterface
    public interface Images {
        /**
         * Called right after the game has loaded all vanilla images
         */
        void onImagesLoading();
    }

    private FileLoadingEvents() {}
}
