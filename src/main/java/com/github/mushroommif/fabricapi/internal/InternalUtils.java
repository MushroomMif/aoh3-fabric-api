package com.github.mushroommif.fabricapi.internal;

import aoh.kingdoms.history.mainGame.Steam.SteamManager;
import aoh.kingdoms.history.map.map.Map;
import com.github.mushroommif.fabricapi.mixin.registry.Map$MapsAccessor;
import net.fabricmc.loader.api.FabricLoader;

import java.util.*;

public final class InternalUtils {
    private static long modCount;

    public static final List<String> builtinMods = new ArrayList<>(4);

    static {
        Collections.addAll(builtinMods, "aoh3", "fabricloader", "mixinextras", "java");
    }

    public static int incrementedModFoldersSize;
    public static List<String> modFoldersNamesWithMark;
    public static final List<Map.Maps> modMaps = new ArrayList<>();
    public static final HashMap<String, Set<String>> modScenarios = new HashMap<>();
    public static final Set<String> modCivilizations = new HashSet<>();

    public static long getModCount() {
        if (modCount == 0) {
            modCount = FabricLoader.getInstance().getAllMods().stream()
                    .filter(mod -> !builtinMods.contains(mod.getMetadata().getId()))
                    .count();
        }

        return modCount;
    }

    public static void cacheModsWithMarkList() {
        incrementedModFoldersSize = SteamManager.modsFolders.size() + 1;

        modFoldersNamesWithMark = new ArrayList<>();
        modFoldersNamesWithMark.addAll(SteamManager.modsFolders);
        modFoldersNamesWithMark.add("$");
    }

    public static Map.Maps createMapMaps(String Folder) {
        Map.Maps maps = new Map.Maps();
        ((Map$MapsAccessor) maps).setFolder(Folder);
        return maps;
    }

    private InternalUtils() {}
}
