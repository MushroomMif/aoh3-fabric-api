package com.github.mushroommif.fabricapi.mixin.resourceloader;

import com.badlogic.gdx.files.FileHandle;
import com.github.mushroommif.fabricapi.FabricApiMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = {
        "aoc.kingdoms.lukasz.jakowski.FileManager$1",
        "aoc.kingdoms.lukasz.jakowski.FileManager$2",
        "aoc.kingdoms.lukasz.jakowski.FileManager$3"
})
public class FileManagerMixin {
    @Inject(method = "loadFile", at = @At("HEAD"), cancellable = true)
    private void loadFromModResources(String sFile, CallbackInfoReturnable<FileHandle> cir) {
        FileHandle loaded = FabricApiMod.modResources.getFile(sFile);
        if (loaded != null) {
            cir.setReturnValue(loaded);
        }
    }
}
