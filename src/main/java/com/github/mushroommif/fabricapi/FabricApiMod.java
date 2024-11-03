package com.github.mushroommif.fabricapi;

import com.github.mushroommif.fabricapi.filemodification.FileModifier;
import com.github.mushroommif.fabricapi.internal.FabricApiImages;
import com.github.mushroommif.fabricapi.internal.InternalUtils;
import com.github.mushroommif.fabricapi.internal.ModFileRepository;
import net.fabricmc.api.ModInitializer;

import java.util.Set;

public class FabricApiMod implements ModInitializer {
    /**
     * Internal field. Should not be used by other mods
     */
    public static final ModFileRepository modResources = new ModFileRepository();

    @Override
    public void onInitialize() {
        FabricApiImages.init();
        registerFileModifiers();
    }

    private void registerFileModifiers() {
        FileModifier.INSTANCE
                .modFilesMatching(name -> name.startsWith("map/") && name.endsWith("/Scenarios.txt"), file -> {
                    String mapName = file.filePath().substring(4);
                    mapName = mapName.substring(0, mapName.indexOf('/'));
                     Set<String> addedScenarios = InternalUtils.modScenarios.get(mapName);

                     if (addedScenarios == null) {
                        return;
                    }

                    file.setCursorAt(file.length());
                    for (String scenario : addedScenarios) {
                        file.add(scenario + ";");
                    }
                })
                .modFile("game/Civilizations.txt", file -> {
                    file.setCursorAt(file.length());
                    for (String civ : InternalUtils.modCivilizations) {
                        file.add(civ + ";");
                    }
                });
    }
}
