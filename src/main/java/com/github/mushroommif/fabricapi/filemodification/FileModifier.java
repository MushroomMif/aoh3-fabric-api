package com.github.mushroommif.fabricapi.filemodification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * An object that can be used to modify game files in runtime and in compatible with other mods way
 */
public final class FileModifier {

    private final Map<String, List<Consumer<Modifiable>>> modifiersByName = new HashMap<>();
    private final List<ByPredicateModifier> modifiersByPredicate = new ArrayList<>();

    public static final FileModifier INSTANCE = new FileModifier();

    /**
     * Modify a file with the exact path as provided
     * @param filePath Path of the modified file
     * @param processor File content processor
     * @return FileModifier instance
     */
    public FileModifier modFile(String filePath, Consumer<Modifiable> processor) {
        modifiersByName
                .computeIfAbsent(filePath, (key) -> new ArrayList<>())
                .add(processor);
        return this;
    }

    /**
     * Modify all files that match the predicate
     * @param fileNamePredicate Predicate to match file names
     * @param processor File content processor
     * @return FileModifier instance
     */
    public FileModifier modFilesMatching(Predicate<String> fileNamePredicate, Consumer<Modifiable> processor) {
        modifiersByPredicate.add(
                new ByPredicateModifier(fileNamePredicate, processor)
        );
        return this;
    }

    /**
     * Apply all registered modifiers to the file with the given name
     * This is an internal method, you should not use it
     * @return Modified content or null if content there is no modifiers for this file
     */
    public String apply(String fileName, Supplier<String> fileContentSupplier) {
        Modifiable modifiableFile = null;
        String fileContent = null;

        List<Consumer<Modifiable>> byNameModifiers = modifiersByName.get(fileName);
        if (byNameModifiers != null) {
            fileContent = fileContentSupplier.get();
            modifiableFile = new ModifiableImpl(fileContent, fileName);
            for (Consumer<Modifiable> processor : byNameModifiers) {
                processor.accept(modifiableFile);
            }
        }

        for (ByPredicateModifier modifier : modifiersByPredicate) {
            if (!modifier.fileNamePredicate.test(fileName)) {
                continue;
            }

            if (fileContent == null) {
                fileContent = fileContentSupplier.get();
            }
            if (modifiableFile == null) {
                modifiableFile = new ModifiableImpl(fileContent, fileName);
            }

            modifier.processor.accept(modifiableFile);
        }

        if (modifiableFile == null) {
            return null;
        }

        return modifiableFile.content();
    }

    private static class ByPredicateModifier {
        public Predicate<String> fileNamePredicate;
        public Consumer<Modifiable> processor;

        public ByPredicateModifier(Predicate<String> fileNamePredicate, Consumer<Modifiable> processor) {
            this.fileNamePredicate = fileNamePredicate;
            this.processor = processor;
        }
    }

    private FileModifier() {}
}
