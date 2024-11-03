package com.github.mushroommif.fabricapi.mixin.event;

import aoc.kingdoms.lukasz.menus.InitGame;
import com.github.mushroommif.fabricapi.event.GameLoadingEvents;
import com.github.mushroommif.fabricapi.event.FileLoadingEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InitGame.class)
public abstract class InitGameMixin {
    @Shadow public abstract void setLoadText(String sText);

    @Inject(
            method = "initGame",
            at = @At(
                    value = "INVOKE",
                    target = "Laoc/kingdoms/lukasz/menus/InitGame;loadSparks()V",
                    shift = At.Shift.AFTER
            )
    )
    private void loadModImages(CallbackInfo ci) {
        if (FileLoadingEvents.IMAGES.handlers().isEmpty()) {
            return;
        }

        this.setLoadText("Loading Images From Mods");
        FileLoadingEvents.IMAGES.invoker().onImagesLoading();
    }

    @Inject(
            method = "initGame",
            at = @At(
                    value = "INVOKE",
                    target = "Laoc/kingdoms/lukasz/menu/MenuManager;setViewID(Laoc/kingdoms/lukasz/menu/View;)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void callLoadedEvent(CallbackInfo ci) {
        GameLoadingEvents.ON_LOADED.invoker().onLoaded();
    }
}
