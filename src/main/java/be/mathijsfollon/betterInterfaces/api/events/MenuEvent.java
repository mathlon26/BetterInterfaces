package be.mathijsfollon.betterInterfaces.api.events;

import be.mathijsfollon.betterInterfaces.api.Menu;
import be.mathijsfollon.betterInterfaces.api.MenuSession;
import be.mathijsfollon.betterInterfaces.api.MenuOpenContextStore;
import org.bukkit.entity.Player;

/**
 * Base interface for all menu-related events.
 */
public interface MenuEvent {
    /**
     * Gets the player associated with this event.
     *
     * @return the player
     */
    Player getPlayer();

    /**
     * Gets the menu associated with this event.
     *
     * @return the menu
     */
    Menu getMenu();

    /**
     * Gets the menu session associated with this event.
     *
     * @return the menu session
     */
    MenuSession getSession();

    /**
     * Gets the menu open context associated with this event.
     *
     * @return the menu open context
     */
    MenuOpenContextStore getContext();

    /**
     * Checks if this event has been cancelled.
     *
     * @return true if cancelled, false otherwise
     */
    boolean isCancelled();

    /**
     * Sets the cancellation state of this event.
     *
     * @param cancelled true to cancel the event, false otherwise
     */
    void setCancelled(boolean cancelled);
}
