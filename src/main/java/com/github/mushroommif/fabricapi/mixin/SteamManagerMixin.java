package com.github.mushroommif.fabricapi.mixin;

import aoh.kingdoms.history.mainGame.Steam.SteamManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(SteamManager.class)
public class SteamManagerMixin {

    /**
     * Dont add jar mods to modsFolders list
     */
    @WrapOperation(
            method = "loadSubscribedItems",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;add(Ljava/lang/Object;)Z"
            )
    )
    private static <E> boolean dontAddJarMods(List<E> instance, E e, Operation<Boolean> original) {
        String[] split = ((String) e).split("\\."); // Doing it so files like .jar1 will also be not included
        if (split[split.length - 1].contains("jar")) {
            return false;
        }

        return original.call(instance, e);
    }
}
