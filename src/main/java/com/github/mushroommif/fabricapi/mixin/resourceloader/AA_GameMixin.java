package com.github.mushroommif.fabricapi.mixin.resourceloader;

import aoc.kingdoms.lukasz.jakowski.AA_Game;
import com.github.mushroommif.fabricapi.FabricApiMod;
import com.github.mushroommif.fabricapi.internal.InternalUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AA_Game.class)
public class AA_GameMixin {
    @Inject(
            method = "create",
            at = @At(
                    value = "INVOKE",
                    target = "Laoc/kingdoms/lukasz/jakowski/Game;loadSettings()V",
                    shift = At.Shift.AFTER
            )
    )
    private void loadResources(CallbackInfo ci) {
        FabricApiMod.modResources.init();
    }

    @Inject(
            method = "create",
            at = @At(
                    value = "INVOKE",
                    target = "Laoc/kingdoms/lukasz/jakowski/Game;loadLanguage()V",
                    shift = At.Shift.BEFORE
            )
    )
    private void loadAllModsCache(CallbackInfo ci) {
        InternalUtils.cacheModsWithMarkList();
    }
}
