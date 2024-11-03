package com.github.mushroommif.fabricapi.event;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class FabricEventImpl<T> implements FabricEvent<T> {
    protected final Function<List<T>, T> invokerFactory;
    protected final LinkedList<T> handlers = new LinkedList<>();
    protected final List<T> unmodifiableHandlers = Collections.unmodifiableList(handlers);

    protected volatile T invoker;

    public FabricEventImpl(Function<List<T>, T> invokerFactory) {
        this.invokerFactory = invokerFactory;
        rebuildInvoker();
    }

    @Override
    public List<T> handlers() {
        return this.handlers;
    }

    @Override
    public T invoker() {
        return this.invoker;
    }

    @Override
    public void register(T handler) {
        Objects.requireNonNull(handler, "Event handler cannot be null");
        handlers.addFirst(handler);
        rebuildInvoker();
    }

    @Override
    public void registerLast(T handler) {
        Objects.requireNonNull(handler, "Event handler cannot be null");
        handlers.addLast(handler);
        rebuildInvoker();
    }

    protected void rebuildInvoker() {
        this.invoker = invokerFactory.apply(unmodifiableHandlers);
    }
}
