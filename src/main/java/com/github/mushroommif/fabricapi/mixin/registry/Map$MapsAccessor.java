package com.github.mushroommif.fabricapi.mixin.registry;

import aoh.kingdoms.history.map.map.Map;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Map.Maps.class)
public interface Map$MapsAccessor {
    @Accessor("Folder")
    String getFolder();

    @Accessor("Folder")
    void setFolder(String Folder);
}
