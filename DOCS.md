[🇷🇺 Русский](https://github.com/MushroomMif/aoh3-fabric-api/blob/master/DOCS_RU.md)
-----
# Resource loader
You can add files to `audio`, `game`, `gfx`, `map` and `ui` folders in
resources of your mod. They will be loaded by the resource loader and will
be accessible in the `FileManager`. If one of provided files would have the
exact path as some vanilla file, file from your mod will override file
from vanilla. Also, you can add `.properties` files (take a look at the
`game/languages` folder in the game directory to see available names) 
to `languages` directory, and they will also be loaded and provided
to the `LanguageManager`.

# Event system
The AOH3 Fabric Api has some useful events that you can listen to. 
You can also create your own events in your mod using this system.
Here is the list of all events at the moment:
- `GameLoadingEvents.ON_LOADED`
- `FileLoadingEvents.IMAGES`
- `ResourceLoaderEvents.SHOULD_LOAD_MOD_RESOURCES`
- `ResourceLoaderEvents.AFTER_RESOURCES_STORE`

You can listen to these events like this:
```java
GameLoadingEvents.ON_LOADED.register(() -> {
    System.out.println("The game was fully loaded!");
});
```
And create a new one using `FabricEvent.create` method

# File modification API
Using this API you can easily edit vanilla (and provided by mods) files 
without overriding them. It means, that more than one mod can modify
the same file. Here is an example of using it (always call `FileModifier`
methods from `onInitialize` method of your mod, or it can be too late):
```java
FileModifier.INSTANCE
        .modFile("game/gameValues/GV_Battle.json", (file) -> {
            file.selectFirst("DRAW_BATTLE_NOT_IN_VIEW: true", (setting) -> {
                setting.replaceFirst("true", "false");
            });
        });
```

# Registry API
This system allows you to easily tell the game to load your custom maps,
scenarios and civilizations. You can use `MapRegistry.addMap`, 
`MapRegistry.addScenario` and `CivilizationRegistry.addCiv` methods