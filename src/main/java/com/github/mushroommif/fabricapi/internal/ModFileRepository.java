package com.github.mushroommif.fabricapi.internal;

import aoc.kingdoms.lukasz.events.EventsManager;
import aoc.kingdoms.lukasz.jakowski.CFG;
import aoc.kingdoms.lukasz.jakowski.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.PropertiesUtils;
import com.github.mushroommif.fabricapi.event.ResourceLoaderEvents;
import com.github.mushroommif.fabricapi.filemodification.FileModifier;
import com.github.mushroommif.fabricapi.mixin.resourceloader.I18NBundleAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class ModFileRepository {
    private final Map<String, FileHandle> files = new HashMap<>();
    public final I18NBundle translations = new I18NBundle();
    private final Map<String, List<byte[]>> translationFilesToLoad = new HashMap<>();
    public Map<String, FileHandle> pseudoFormableCivsFolders = new HashMap<>();
    private Map<String, List<FileHandle>> formableCivsFiles = new HashMap<>();
    private final Map<String, byte[]> modifiedFilesContent = new HashMap<>();

    private final File extractedResourcesDir = new File(".fabric/extracted_mod_resources");
    private final List<String> loadDirectories = new ArrayList<>(5);
    private byte[] currentFileContent = null;

    public ModFileRepository() {
        Collections.addAll(loadDirectories, "audio/", "game/", "gfx/", "map/", "ui/");
        ((I18NBundleAccessor) translations).setProperties(new ObjectMap<>());
    }

    public FileHandle getFile(String name) {
        return files.get(name);
    }

    public void init() {
        clearTempDir();
        registerEventHandlers();

        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            if (!shouldLoadModResources(mod)) {
                continue;
            }
            String modId = mod.getMetadata().getId();

            for (Path jarPath : mod.getOrigin().getPaths()) {
                try (InputStream fis = Files.newInputStream(jarPath);
                     ZipInputStream zis = new ZipInputStream(fis)) {

                    ZipEntry entry;
                    while ((entry = zis.getNextEntry()) != null) {
                        if (entry.isDirectory()) {
                            continue;
                        }
                        String fileName = entry.getName();

                        boolean shouldStore = ResourceLoaderEvents.ON_STORE_RESOURCE.invoker().onStoreResource(
                                mod, fileName, () -> {
                                    if (currentFileContent != null) {
                                        return currentFileContent;
                                    }

                                    try {
                                        return (currentFileContent = loadZipEntryData(zis));
                                    } catch (IOException e) {
                                        Gdx.app.error("Mod Resource Loader", "Failed to load data of "
                                                + fileName + " resource file from " + modId + " mod", e);
                                        return new byte[0];
                                    }
                                }
                        );

                        if (!shouldStore) {
                            currentFileContent = null;
                            continue;
                        }

                        File file = new File(extractedResourcesDir, fileName);
                        if (file.exists()) {
                            currentFileContent = null;
                            continue;
                        }

                        if (currentFileContent == null) {
                            currentFileContent = loadZipEntryData(zis);
                        }

                        String modifiedFileContent = FileModifier.INSTANCE.apply(
                                fileName, () -> new String(currentFileContent, StandardCharsets.UTF_8)
                        );

                        FileHandle fileHandle = new FileHandle(file);

                        if (modifiedFileContent != null) {
                            fileHandle.writeString(modifiedFileContent, true);
                        } else {
                            fileHandle.writeBytes(currentFileContent, true);
                        }

                        files.put(fileName, fileHandle);
                        currentFileContent = null;
                    }
                } catch (Exception e) {
                    Gdx.app.error("Mod Resource Loader", "Failed to load " + modId + " mod resources", e);
                }
            }
        }

        ResourceLoaderEvents.AFTER_RESOURCES_STORE.invoker().afterResourcesStore();
    }

    public void loadFormableCiv(String resourcePath, byte[] resourceData) {
        String mapName = getMapName(resourcePath);
        formableCivsFiles.computeIfAbsent(mapName, (key) -> new ArrayList<>())
                .add(new FileHandle() {
                    private final String data = new String(resourceData, StandardCharsets.UTF_8);

                    @Override
                    public String readString() {
                        return data;
                    }

                    @Override
                    public String path() {
                        return resourcePath;
                    }
                });
    }

    public void loadTranslations() {
        String userLanguage = Game.settingsManager.LANGUAGE_TAG;
        if (userLanguage == null) {
            return;
        }

        ((I18NBundleAccessor) translations).invokeSetLocale(new Locale(userLanguage));
        loadTranslations("");
        if (!userLanguage.equals("")) {
            loadTranslations(userLanguage);
        }

        translationFilesToLoad.clear();
    }

    public byte[] getModifiedContent(FileHandle file, InputStream originalInput) {
        String path = file.path();
        // Files loaded from mods are already modified
        if (path.length() > 32 && files.containsKey(path.substring(32))) {
            return null;
        }

        if (modifiedFilesContent.containsKey(path)) {
            return modifiedFilesContent.get(path);
        }

        String newContent = FileModifier.INSTANCE.apply(path, () -> readString(originalInput));
        byte[] newBytes = null;
        if (newContent != null) {
            newBytes = newContent.getBytes(StandardCharsets.UTF_8);
        }
        modifiedFilesContent.put(path, newBytes);

        return newBytes;
    }

    private String readString(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            CFG.exceptionStack(e);
        }

        return stringBuilder.toString().trim();
    }

    private void loadTranslations(String languageCode) {
        for (byte[] translationFileData : translationFilesToLoad.getOrDefault(languageCode, Collections.emptyList())) {
            Reader translationReader = new InputStreamReader(
                    new ByteArrayInputStream(translationFileData),
                    StandardCharsets.UTF_8
            );

            try {
                PropertiesUtils.load(
                        ((I18NBundleAccessor) translations).getProperties(), translationReader
                );
            } catch (IOException e) {
                CFG.exceptionStack(e);
            }
        }
    }

    private void loadEvent(String modId, String filePath, Supplier<byte[]> dataSupplier) {
        String eventType = filePath.substring(12);
        eventType = eventType.substring(0, filePath.indexOf('/'));

        int eventTypeId;
        // TODO: Add API to create custom event types
        switch (eventType) {
            case "common":
                eventTypeId = 0;
                break;
            case "siege":
                eventTypeId = 1;
                break;
            case "global":
                eventTypeId = 2;
                break;
            default:
                Gdx.app.error("Resource Loader",
                        "Got invalid event type " + eventType + " for " + filePath + " in " + modId + " mod");
                return;
        }

        String[] eventDef = new String(dataSupplier.get(), StandardCharsets.UTF_8)
                .split("\\r?\\n");
        EventsManager.loadEvent(eventTypeId, eventDef);
    }

    private boolean shouldLoadModResources(ModContainer mod) {
        switch (ResourceLoaderEvents.SHOULD_LOAD_MOD_RESOURCES.invoker().shouldLoadResources(mod)) {
            case SUCCESS:
                return true;
            case FAIL:
                return false;
            default:
                return !InternalUtils.builtinMods.contains(mod.getMetadata().getId());
        }
    }

    private byte[] loadZipEntryData(ZipInputStream entryInput) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = entryInput.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }

        return baos.toByteArray();
    }

    private void registerEventHandlers() {
        ResourceLoaderEvents.ON_STORE_RESOURCE.registerLast((mod, resourcePath, resourceDataSupplier) -> {
            String languageCode = getLanguageCode(resourcePath);
            if (languageCode == null) {
                return true;
            }

            translationFilesToLoad.computeIfAbsent(languageCode, (key) -> new ArrayList<>())
                    .add(resourceDataSupplier.get());

            return false;
        });

        ResourceLoaderEvents.ON_STORE_RESOURCE.register((mod, resourcePath, resourceDataSupplier) -> {
            if (!resourcePath.startsWith("game/events/")) {
                return true;
            }

            loadEvent(mod.getMetadata().getId(), resourcePath, resourceDataSupplier);
            return false;
        });

        ResourceLoaderEvents.ON_STORE_RESOURCE.register((mod, resourcePath, resourceDataSupplier) -> {
            if (!(resourcePath.startsWith("map/") && resourcePath.contains("/formableCivs/"))) {
                return true;
            }

            loadFormableCiv(resourcePath, resourceDataSupplier.get());
            return false;
        });

        ResourceLoaderEvents.ON_STORE_RESOURCE.registerLast((mod, resourcePath, resourceDataSupplier) -> {
            for (String loadDirectoryName : loadDirectories) {
                if (resourcePath.startsWith(loadDirectoryName)) {
                    return true;
                }
            }

            return false;
        });

        ResourceLoaderEvents.AFTER_RESOURCES_STORE.registerLast(() -> loadTranslations());

        ResourceLoaderEvents.AFTER_RESOURCES_STORE.registerLast(() -> {
            formableCivsFiles.forEach((mapName, formableCivs) -> {
                pseudoFormableCivsFolders.put(mapName, new FileHandle() {
                    private final FileHandle[] content = formableCivs.toArray(new FileHandle[0]);

                    @Override
                    public FileHandle[] list() {
                        return content;
                    }
                });
            });

            formableCivsFiles = null;
        });
    }

    private String getLanguageCode(String translationFilePath) {
        if (!(translationFilePath.startsWith("languages/Bundle") && translationFilePath.endsWith(".properties"))) {
            return null;
        }

        if (translationFilePath.equals("languages/Bundle.properties")) {
            return "";
        }

        return translationFilePath.substring(17, translationFilePath.length() - 11);
    }

    private String getMapName(String formableCivFilePath) {
        formableCivFilePath = formableCivFilePath.substring(4);
        return formableCivFilePath
                .substring(0, formableCivFilePath.indexOf('/'));
    }

    private void clearTempDir() {
        try {
            Files.walkFileTree(Paths.get(extractedResourcesDir.getAbsolutePath()), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to clear temporary mod resources directory", e);
        }
    }
}
