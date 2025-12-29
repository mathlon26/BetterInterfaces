package be.mathijsfollon.betterInterfaces.events;

import be.mathijsfollon.betterInterfaces.api.MenuOpenContextStore;
import be.mathijsfollon.betterInterfaces.api.events.MenuEvent;
import be.mathijsfollon.betterInterfaces.api.events.MenuEventHandler;
import be.mathijsfollon.betterInterfaces.api.events.MenuEventListener;
import be.mathijsfollon.betterInterfaces.api.events.MenuEventManager;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages menu event listeners and event dispatching.
 */
public class MenuEventManagerImpl implements MenuEventManager {
    private final Map<Class<? extends MenuEvent>, List<EventHandler>> handlers = new ConcurrentHashMap<>();

    /**
     * Registers a listener and scans it for methods annotated with {@link MenuEventHandler}.
     *
     * @param listener the listener to register
     */
    @Override
    public void registerListener(MenuEventListener listener) {
        Class<?> clazz = listener.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            MenuEventHandler annotation = method.getAnnotation(MenuEventHandler.class);
            if (annotation == null) {
                continue;
            }

            // Validate method signature
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length != 1 || !MenuEvent.class.isAssignableFrom(parameterTypes[0])) {
                continue;
            }

            @SuppressWarnings("unchecked")
            Class<? extends MenuEvent> eventType = (Class<? extends MenuEvent>) parameterTypes[0];

            method.setAccessible(true);
            EventHandler handler = new EventHandler(listener, method, annotation.priority(), annotation.ignoreCancelled());

            handlers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(handler);
            
            // Sort by priority (lower priority first)
            List<EventHandler> handlerList = handlers.get(eventType);
            handlerList.sort(Comparator.comparingInt(EventHandler::priority));
        }
    }

    /**
     * Unregisters a listener, removing all its event handlers.
     *
     * @param listener the listener to unregister
     */
    @Override
    public void unregisterListener(MenuEventListener listener) {
        for (List<EventHandler> handlerList : handlers.values()) {
            handlerList.removeIf(handler -> handler.listener() == listener);
        }
    }

    /**
     * Dispatches an event to all registered listeners.
     *
     * @param event the event to dispatch
     */
    @Override
    public void fireEvent(MenuEvent event) {
        Class<? extends MenuEvent> eventType = event.getClass();
        List<EventHandler> handlerList = handlers.get(eventType);
        
        if (handlerList == null || handlerList.isEmpty()) {
            return;
        }

        // Get the MenuDefinition class from context to match against listener classes
        MenuOpenContextStore context = event.getContext();
        Optional<Class<?>> menuDefinitionClass = context.getMenuDefinitionClass();

        for (EventHandler handler : handlerList) {
            // Only invoke handlers if the listener's class matches the MenuDefinition class that created the menu
            if (menuDefinitionClass.isPresent()) {
                Class<?> listenerClass = handler.listener().getClass();
                Class<?> definitionClass = menuDefinitionClass.get();
                
                // Match if listener class equals the MenuDefinition class
                // This ensures only the MenuDefinition that created the menu handles its events
                if (!listenerClass.equals(definitionClass)) {
                    continue;
                }
            }

            // Skip if cancelled and handler ignores cancelled events
            if (event.isCancelled() && handler.ignoreCancelled()) {
                continue;
            }

            try {
                handler.method().invoke(handler.listener(), event);
            } catch (Exception e) {
                // Log error but continue processing other handlers
                System.err.println("Error invoking event handler " + handler.method().getName() + 
                        " in " + handler.listener().getClass().getName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Clears all registered listeners.
     */
    @Override
    public void clear() {
        handlers.clear();
    }

    /**
     * Internal record to store event handler information.
     */
    private record EventHandler(
            MenuEventListener listener,
            Method method,
            int priority,
            boolean ignoreCancelled
    ) {}
}

