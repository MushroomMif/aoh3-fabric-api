package com.github.mushroommif.fabricapi.registry;

import aoh.kingdoms.history.map.map.Map;
import com.github.mushroommif.fabricapi.internal.InternalUtils;
import com.github.mushroommif.fabricapi.mixin.registry.Map$MapsAccessor;

import java.util.HashSet;

/**
 * A utility class to add data about custom maps and scenarios
 */
public final class MapRegistry {
    /**
     * Inform the game to load a map with provided name
     * @param mapFolderName Name of map's folder inside maps folder
     */
    public static void addMap(String mapFolderName) {
        for (Map.Maps maps : InternalUtils.modMaps) {
            if (((Map$MapsAccessor) maps).getFolder().equals(mapFolderName)) {
                throw new IllegalArgumentException(mapFolderName + " map was already registered");
            }
        }

        InternalUtils.modMaps.add(
                InternalUtils.createMapMaps(mapFolderName)
        );
    }

    /**
     * Inform the game to load a scenario with provided name
     * @param mapName Name of map to load scenario for
     * @param scenarioName Name of scenario
     */
    public static void addScenario(String mapName, String scenarioName) {
        boolean addResult = InternalUtils.modScenarios
                .computeIfAbsent(mapName, (key) -> new HashSet<>())
                .add(scenarioName);

        if (!addResult) {
            throw new IllegalArgumentException(scenarioName + " scenario was already registered for " + mapName + " map");
        }
    }

    private MapRegistry() {}
}
