package com.github.mushroommif.fabricapi.mixin.resourceloader;

import aoc.kingdoms.lukasz.textures.ImageManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.github.mushroommif.fabricapi.FabricApiMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ImageManager.class)
public class ImageManagerMixin {
    @Inject(
            method = "loadTexture(Ljava/lang/String;Lcom/badlogic/gdx/graphics/Pixmap$Format;)Lcom/badlogic/gdx/graphics/Texture;",
            at = @At(
                    value = "INVOKE",
                    target = "Laoc/kingdoms/lukasz/jakowski/CFG;isDesktop()Z"
            ),
            cancellable = true
    )
    private static void loadModTextures(String sFile, Pixmap.Format nFormat, CallbackInfoReturnable<Texture> cir) {
        FileHandle resourceFile = FabricApiMod.modResources.getFile(sFile);
        if (resourceFile != null) {
            try {
                cir.setReturnValue(new Texture(resourceFile, nFormat, false));
            } catch (Exception e) {
                System.out.println("Here");
                e.printStackTrace();
            }
        }
    }
}
