package com.github.mushroommif.fabricapi.mixin.resourceloader;

import aoh.kingdoms.history.mainGame.LanguageManager;
import com.github.mushroommif.fabricapi.FabricApiMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.MissingResourceException;

@Mixin(LanguageManager.class)
public class LanguageManagerMixin {
    @Unique
    private static boolean fabricApi$returnCustomTranslation(String key, CallbackInfoReturnable<String> cir) {
        String translation = ((I18NBundleAccessor) FabricApiMod.modResources.translations)
                .getProperties().get(key);

        if (translation != null) {
            cir.setReturnValue(translation);
            return true;
        }

        return false;
    }

    @Inject(method = "getCiv", at = @At("HEAD"), cancellable = true)
    private void getCustomCivTranslation(String key, CallbackInfoReturnable<String> cir) {
        if (fabricApi$returnCustomTranslation(key, cir)) {
            return;
        }

        // original game code does this for some reason so we also need to do it
        int weirdIndex = key.indexOf(95);
        if (weirdIndex > 0) {
            fabricApi$returnCustomTranslation(key.substring(0, weirdIndex), cir);
        }
    }

    @Inject(method = "getLoading", at = @At("HEAD"), cancellable = true)
    private void getCustomLoadingTranslation(String key, CallbackInfoReturnable<String> cir) {
        fabricApi$returnCustomTranslation(key, cir);
    }

    @Inject(method = "get(Ljava/lang/String;)Ljava/lang/String;", at = @At("HEAD"), cancellable = true)
    private void getCustomTranslation(String key, CallbackInfoReturnable<String> cir) {
        fabricApi$returnCustomTranslation(key, cir);
    }

    @Inject(method = "get(Ljava/lang/String;I)Ljava/lang/String;", at = @At("HEAD"), cancellable = true)
    private void getCustomTranslation(String key, int iValue, CallbackInfoReturnable<String> cir) {
        try {
            cir.setReturnValue(FabricApiMod.modResources.translations.format(key, iValue));
        } catch (MissingResourceException ignored) {

        }
    }

    @Inject(method = "get(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", at = @At("HEAD"), cancellable = true)
    private void getCustomTranslation(String key, String sValue, CallbackInfoReturnable<String> cir) {
        try {
            cir.setReturnValue(FabricApiMod.modResources.translations.format(key, sValue));
        } catch (MissingResourceException ignored) {

        }
    }

    @Inject(method = "get(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", at = @At("HEAD"), cancellable = true)
    private void getCustomTranslation(String key, String sValue, String sValue2, CallbackInfoReturnable<String> cir) {
        try {
            cir.setReturnValue(FabricApiMod.modResources.translations.format(key, sValue, sValue2));
        } catch (MissingResourceException ignored) {

        }
    }
}
