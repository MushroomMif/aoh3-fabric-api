package com.github.mushroommif.fabricapi.mixin.registry;

import aoc.kingdoms.lukasz.map.map.Map;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.ArrayList;

@Mixin(Map.Config.class)
public interface Map$ConfigAccessor {
    @Accessor("Map")
    ArrayList getMap();
}
