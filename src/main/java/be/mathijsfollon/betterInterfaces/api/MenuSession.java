package be.mathijsfollon.betterInterfaces.api;

import be.mathijsfollon.betterInterfaces.api.events.MenuEventManager;
import be.mathijsfollon.betterInterfaces.events.MenuOpenEvent;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface MenuSession {
    boolean isOpen();

    void open();

    void close();

    void close(boolean silently);

    CompletableFuture<MenuOpenEvent> getResult();

    /**
     * Gets the event manager associated with this session.
     * Can be used to fire custom events.
     *
     * @return the event manager
     */
    MenuEventManager getEventManager();

    /**
     * Gets the previous menu session in the navigation stack.
     *
     * @return Optional containing the previous menu session, or empty if none exists
     */
    Optional<MenuSession> getPreviousSession();

    /**
     * Navigates back to the previous menu if one exists.
     * Closes the current menu and opens the previous one.
     *
     * @return true if navigation was successful, false if no previous menu
     */
    boolean goBack();
}
