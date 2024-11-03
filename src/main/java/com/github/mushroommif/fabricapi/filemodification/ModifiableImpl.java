package com.github.mushroommif.fabricapi.filemodification;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 95% of this file was written by ChatGPT
public class ModifiableImpl implements Modifiable {
    private final StringBuilder content;
    private final String filePath;
    private int start;
    private int end;
    private int cursor;

    public ModifiableImpl(String initialContent, String filePath) {
        this.content = new StringBuilder(initialContent);
        this.filePath = filePath;
        this.start = 0;
        this.end = content.length();
        this.cursor = 0;
    }

    @Override
    public String content() {
        return content.substring(start, end);
    }

    @Override
    public int length() {
        return end - start;
    }

    @Override
    public String filePath() {
        return filePath;
    }

    @Override
    public void setContent(String newContent) {
        content.replace(start, end, newContent);
        end = start + newContent.length();
    }

    @Override
    public void setCursorAt(int index) {
        if (index >= 0 && index <= (end - start)) {
            cursor = start + index;
        } else {
            throw new IndexOutOfBoundsException("Invalid cursor position: " + index);
        }
    }

    @Override
    public void add(String toAdd) {
        content.insert(cursor, toAdd);
        cursor += toAdd.length();
        end += toAdd.length();
    }

    @Override
    public Modifiable selectFirst(String target, Consumer<Modifiable> processor) {
        int index = content.indexOf(target, start);
        if (index != -1 && index < end) {
            applySubselection(index, index + target.length(), processor);
        }
        return this;
    }

    @Override
    public Modifiable selectAll(String target, Consumer<Modifiable> processor) {
        int index = content.indexOf(target, start);
        while (index != -1 && index < end) {
            applySubselection(index, index + target.length(), processor);
            index = content.indexOf(target, index + target.length());
        }
        return this;
    }

    @Override
    public Modifiable selectFirst(Pattern regex, Consumer<Modifiable> processor) {
        Matcher matcher = regex.matcher(content.subSequence(start, end));
        if (matcher.find()) {
            int matchStart = start + matcher.start();
            int matchEnd = start + matcher.end();
            applySubselection(matchStart, matchEnd, processor);
        }
        return this;
    }

    @Override
    public Modifiable selectAll(Pattern regex, Consumer<Modifiable> processor) {
        Matcher matcher = regex.matcher(content.subSequence(start, end));
        while (matcher.find()) {
            int matchStart = start + matcher.start();
            int matchEnd = start + matcher.end();
            applySubselection(matchStart, matchEnd, processor);
        }
        return this;
    }

    private void applySubselection(int newStart, int newEnd, Consumer<Modifiable> processor) {
        int previousStart = this.start;
        int previousEnd = this.end;
        int previousCursor = this.cursor;

        this.start = newStart;
        this.end = newEnd;
        this.cursor = this.start;

        processor.accept(this);

        this.start = previousStart;
        this.end = previousEnd + (this.end - newEnd);
        this.cursor = previousCursor;
    }
}
