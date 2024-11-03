package com.github.mushroommif.fabricapi.filemodification;

import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * A file or a part of a file that can be modified
 */
public interface Modifiable {
    /**
     * @return Content of the current segment
     */
    String content();

    /**
     * @return Length of the current segment
     */
    int length();

    /**
     * @return Path of the modified file
     */
    String filePath();

    /**
     * Set content of the current segment
     * @param newContent New content
     */
    void setContent(String newContent);

    /**
     * Set cursor at the specified index to use {@link Modifiable#add} method at the correct place
     * @param index Index to place the cursor at
     * @throws IllegalArgumentException if index is not positive or too large
     */
    void setCursorAt(int index);

    /**
     * Write string to the specified by {@link Modifiable#setCursorAt} place.
     * By default, it is the start of current segment
     * @param toAdd String to add
     */
    void add(String toAdd);

    /**
     * Selects the first occurrence of the specified target in the current segment
     * @param target Target to select
     * @param processor Processor to apply to the selected segment
     * @return This modifiable
     */
    Modifiable selectFirst(String target, Consumer<Modifiable> processor);

    /**
     * Selects all occurrences of the specified target in the current segment
     * @param target Target to select
     * @param processor Processor to apply to the selected segments
     * @return This modifiable
     */
    Modifiable selectAll(String target, Consumer<Modifiable> processor);

    /**
     * Selects the first occurrence of the specified regex in the current segment
     * @param regex Regex to select
     * @param processor Processor to apply to the selected segment
     * @return This modifiable
     */
    Modifiable selectFirst(Pattern regex, Consumer<Modifiable> processor);

    /**
     * Selects all occurrences of the specified regex in the current segment
     * @param regex Regex to select
     * @param processor Processor to apply to the selected segments
     * @return This modifiable
     */
    Modifiable selectAll(Pattern regex, Consumer<Modifiable> processor);

    /**
     * Replaces the first occurrence of the specified target in the current segment with the specified string
     * @param target Target to replace
     * @param replaceWith String to replace target with
     */
    default void replaceFirst(String target, String replaceWith) {
        selectFirst(target, (toReplace) -> toReplace.setContent(replaceWith));
    }

    /**
     * Replaces all occurrences of the specified target in the current segment with the specified string
     * @param target Target to replace
     * @param replaceWith String to replace target with
     */
    default void replaceAll(String target, String replaceWith) {
        selectAll(target, (toReplace) -> toReplace.setContent(replaceWith));
    }

    /**
     * Replaces the first occurrence of the specified regex in the current segment with the specified string
     * @param regex Regex to replace
     * @param replaceWith String to replace regex with
     */
    default void replaceFirst(Pattern regex, CharSequence replaceWith) {
        selectFirst(regex, (toReplace) -> toReplace.setContent(replaceWith.toString()));
    }

    /**
     * Replaces all occurrences of the specified regex in the current segment with the specified string
     * @param regex Regex to replace
     * @param replaceWith String to replace regex with
     */
    default void replaceAll(Pattern regex, CharSequence replaceWith) {
        selectAll(regex, (toReplace) -> toReplace.setContent(replaceWith.toString()));
    }
}
