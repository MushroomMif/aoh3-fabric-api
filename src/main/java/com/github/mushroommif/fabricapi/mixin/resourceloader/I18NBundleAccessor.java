package com.github.mushroommif.fabricapi.mixin.resourceloader;

import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ObjectMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Locale;

@Mixin(I18NBundle.class)
public interface I18NBundleAccessor {
    @Accessor
    ObjectMap<String, String> getProperties();

    @Accessor
    void setProperties(ObjectMap<String, String> properties);

    @Invoker
    void invokeSetLocale(Locale locale);
}
