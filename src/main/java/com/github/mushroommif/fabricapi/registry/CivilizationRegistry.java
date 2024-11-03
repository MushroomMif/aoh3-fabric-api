package com.github.mushroommif.fabricapi.registry;

import com.github.mushroommif.fabricapi.internal.InternalUtils;

/**
 * A utility class to add data about custom civilization tags
 */
public final class CivilizationRegistry {
    /**
     * Inform the game about custom civilization tag
     * @param civTag Civilization tag
     */
    public static void addCiv(String civTag) {
        if (!InternalUtils.modCivilizations.add(civTag)) {
            throw new IllegalArgumentException(civTag + " civilization was already registered");
        }
    }

    private CivilizationRegistry() {}
}
