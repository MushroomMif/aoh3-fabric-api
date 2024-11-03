package com.github.mushroommif.fabricapi.mixin.resourceloader;

import com.github.mushroommif.fabricapi.FabricApiMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "aoc.kingdoms.lukasz.menus.Init_SelectLanguage$1")
public class Init_SelectLanguage$1Mixin {
    @Inject(
            method = "actionElement",
            at = @At(
                    value = "NEW",
                    target = "(Ljava/lang/String;)Laoc/kingdoms/lukasz/jakowski/LanguageManager;"
            )
    )
    private void loadTranslationsOnFirstLanguageSelect(CallbackInfo ci) {
        FabricApiMod.modResources.loadTranslations();
    }
}
