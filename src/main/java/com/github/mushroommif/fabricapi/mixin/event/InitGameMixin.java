package com.github.mushroommif.fabricapi.mixin.event;

import aoh.kingdoms.history.menus.InitGame;
import com.github.mushroommif.fabricapi.event.FileLoadingEvents;
import com.github.mushroommif.fabricapi.event.GameLoadingEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InitGame.class)
public abstract class InitGameMixin {

    @Unique
    private static boolean fabricApi$vanillaImages8Loaded = false;

    @Unique
    private static boolean fabricApi$customModFilesLoadingTextSet = false;

    @Shadow public abstract void setLoadText(String sText);

    @Shadow public static int iStepID;

    // Invoking listeners of this event in this strange way because we need second draw method call to apply custom load text
    @Inject(
            method = "initGame",
            at = @At(
                    value = "INVOKE",
                    target = "Laoh/kingdoms/history/menus/InitGame;loadImages_8()V",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void loadModImages(CallbackInfo ci) {
        if (!fabricApi$vanillaImages8Loaded) {
            return;
        }

        FileLoadingEvents.IMAGES.invoker().onImagesLoading();
        this.setLoadText("Loading Province Map Data");
        iStepID++;
        ci.cancel();
    }

    @Inject(
            method = "initGame",
            at = @At(
                    value = "INVOKE",
                    target = "Laoh/kingdoms/history/menus/InitGame;loadSparks()V",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private void setModImagesLoadingText(CallbackInfo ci) {
        if (FileLoadingEvents.IMAGES.handlers().isEmpty()) {
            return;
        }

        fabricApi$vanillaImages8Loaded = true;
        this.setLoadText("Loading Images From Mods");
        ci.cancel();
    }

    @Inject(
            method = "initGame",
            at = @At(
                    value = "INVOKE",
                    target = "Laoh/kingdoms/history/menu/MenuManager;setViewID(Laoh/kingdoms/history/menu/View;)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void callLoadedEvent(CallbackInfo ci) {
        GameLoadingEvents.ON_LOADED.invoker().onLoaded();
    }

    @Inject(
            method = "initGame",
            at = @At(
                    value = "INVOKE",
                    target = "Laoh/kingdoms/history/map/map/MapScale;setEnableScaling(Z)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void loadModCustomFiles(CallbackInfo ci) {
        if (!fabricApi$customModFilesLoadingTextSet) {
            return;
        }

        FileLoadingEvents.END.invoker().onEndLoading();
    }

    @Inject(
            method = "initGame",
            at = @At(
                    value = "INVOKE",
                    target = "Laoh/kingdoms/history/map/map/MapScale;setEnableScaling(Z)V",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void setModCustomFilesLoadingText(CallbackInfo ci) {
        if (fabricApi$customModFilesLoadingTextSet || FileLoadingEvents.IMAGES.handlers().isEmpty()) {
            return;
        }

        this.setLoadText("Loading Mods Custom Files");
        fabricApi$customModFilesLoadingTextSet = true;
        ci.cancel();
    }
}
