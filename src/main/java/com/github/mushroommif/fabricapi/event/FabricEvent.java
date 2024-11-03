package com.github.mushroommif.fabricapi.event;

import java.util.List;
import java.util.function.Function;

/**
 * An event that can be listened and invoked
 * @param <T> Event handler
 */
public interface FabricEvent<T> {
    /**
     * @return List of all registered handlers
     */
    List<T> handlers();

    /**
     * @return An event handler object that will invoke all registered listeners
     */
    T invoker();

    /**
     * Register an event handler and put it first in the line
     * @param handler Event handler
     */
    void register(T handler);

    /**
     * Register an event handler and put it last in the line
     * @param handler Event handler
     */
    void registerLast(T handler);

    /**
     * Create a new event using default implementation
     * @param invokerFactory A function that will provide a new invoker object every time new handler was registered
     * @return An event object
     * @param <T> Event handler
     */
    static <T> FabricEvent<T> create(Function<List<T>, T> invokerFactory) {
        return new FabricEventImpl<>(invokerFactory);
    }
}
