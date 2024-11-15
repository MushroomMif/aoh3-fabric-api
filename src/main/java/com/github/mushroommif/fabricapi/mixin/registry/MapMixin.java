package com.github.mushroommif.fabricapi.mixin.registry;

import aoh.kingdoms.history.map.map.Map;
import com.github.mushroommif.fabricapi.internal.InternalUtils;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Map.class)
public class MapMixin {
    @Inject(
            method = "<init>",
            at = @At(
                    value = "NEW",
                    target = "()Ljava/util/ArrayList;",
                    shift = At.Shift.AFTER
            )
    )
    private void addModMaps(CallbackInfo ci, @Local(ordinal = 0) Map.Config data) {
        ((Map$ConfigAccessor) data).getMap().addAll(InternalUtils.modMaps);
    }
}
