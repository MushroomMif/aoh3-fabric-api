package com.github.mushroommif.fabricapi.internal;

import aoh.kingdoms.history.textures.ImageManager;
import com.github.mushroommif.fabricapi.event.FileLoadingEvents;

public final class FabricApiImages {
    public static int fabricLogo;

    public static void init() {
        FileLoadingEvents.IMAGES.register(() -> {
            fabricLogo = ImageManager.addImage("ui/fabric-api/icons/fabric_logo.png");
        });
    }

    private FabricApiImages() {}
}
