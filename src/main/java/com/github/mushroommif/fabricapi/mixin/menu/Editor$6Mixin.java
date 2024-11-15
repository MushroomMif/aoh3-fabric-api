package com.github.mushroommif.fabricapi.mixin.menu;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "aoh.kingdoms.history.menusEditor.Editor$6")
public class Editor$6Mixin {
    @ModifyArg(
            method = "updateLanguage",
            at = @At(
                    value = "INVOKE",
                    target = "Laoh/kingdoms/history/mainGame/LanguageManager;get(Ljava/lang/String;)Ljava/lang/String;"
            ),
            index = 0
    )
    private String changeButtonName(String key) {
        return "fabric-api.ManageVanillaMods";
    }

    @ModifyArg(
            method = "buildElementHover",
            at = @At(
                    value = "INVOKE",
                    target = "Laoh/kingdoms/history/mainGame/LanguageManager;get(Ljava/lang/String;)Ljava/lang/String;"
            ),
            index = 0
    )
    private String changeButtonNameInHover(String key) {
        return "fabric-api.ManageVanillaMods";
    }
}
