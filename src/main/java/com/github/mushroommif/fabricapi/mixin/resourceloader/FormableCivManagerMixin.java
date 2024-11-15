package com.github.mushroommif.fabricapi.mixin.resourceloader;

import aoh.kingdoms.history.map.FormableCivManager;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;
import com.github.mushroommif.fabricapi.FabricApiMod;
import com.github.mushroommif.fabricapi.internal.InternalUtils;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(FormableCivManager.class)
public class FormableCivManagerMixin {
    @WrapOperation(
            method = "buildFormableCivilizations",
            at = @At(
                    value = "FIELD",
                    target = "Laoh/kingdoms/history/mainGame/Steam/SteamManager;modsFoldersSize:I"
            )
    )
    private static int returnCustomModFolderCount(Operation<Integer> original) {
        return InternalUtils.incrementedModFoldersSize;
    }

    @WrapOperation(
            method = "buildFormableCivilizations",
            at = @At(
                    value = "FIELD",
                    target = "Laoh/kingdoms/history/mainGame/Steam/SteamManager;modsFolders:Ljava/util/List;"
            )
    )
    private static List<String> returnCustomListWithMark(Operation<List<String>> original) {
        return InternalUtils.modFoldersNamesWithMark;
    }

    @WrapOperation(
            method = "buildFormableCivilizations",
            at = {
                    @At(
                            value = "INVOKE",
                            target = "Lcom/badlogic/gdx/Files;external(Ljava/lang/String;)Lcom/badlogic/gdx/files/FileHandle;"
                    ),
                    @At(
                            value = "INVOKE",
                            target = "Lcom/badlogic/gdx/Files;internal(Ljava/lang/String;)Lcom/badlogic/gdx/files/FileHandle;"
                    )
            }
    )
    private static FileHandle returnCustomPseudoFolder(Files instance, String s, Operation<FileHandle> original) {
        if (s.startsWith("$")) {
            String mapName = s.substring(5);
            mapName = mapName.substring(0, mapName.indexOf('/'));

            FileHandle customPseudoFolder = FabricApiMod.modResources.pseudoFormableCivsFolders.get(mapName);
            if (customPseudoFolder != null) {
                return customPseudoFolder;
            }
        }

        return original.call(instance, s);
    }
}
