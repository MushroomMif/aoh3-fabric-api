package com.github.mushroommif.fabricapi.mixin.resourceloader;

import com.badlogic.gdx.files.FileHandle;
import com.github.mushroommif.fabricapi.FabricApiMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Mixin(FileHandle.class)
public class FileHandleMixin {
    @Inject(
            method = "read()Ljava/io/InputStream;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void returnModifiedStream(CallbackInfoReturnable<InputStream> cir) {
        byte[] modifiedContent = FabricApiMod.modResources.getModifiedContent(
                (FileHandle) (Object) this, cir.getReturnValue()
        );
        if (modifiedContent == null) {
            return;
        }

        cir.setReturnValue(
                new ByteArrayInputStream(modifiedContent)
        );
    }
}
