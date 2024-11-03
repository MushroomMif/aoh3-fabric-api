package com.github.mushroommif.fabricapi.event;

public class GameLoadingEvents {

    public static final FabricEvent<OnLoaded> ON_LOADED = FabricEvent.create(listeners -> () -> {
        for (OnLoaded listener : listeners) {
            listener.onLoaded();
        }
    });

    public interface OnLoaded {
        /**
         * Called after the game was fully loaded but before switch to main menu
         */
        void onLoaded();
    }
}
