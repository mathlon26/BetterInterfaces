package be.mathijsfollon.betterInterfaces.api;

import be.mathijsfollon.betterInterfaces.api.util.Store;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * Context data passed when opening a menu.
 * Provides a thread-safe key-value store for passing custom data to menu definitions.
 * 
 * This interface extends Store for basic key-value operations and adds
 * convenience methods for common operations like getting the player.
 */
public interface MenuOpenContextStore extends Store<String, Object> {
    /**
     * Get a typed value from the context.
     *
     * @param key   the key
     * @param clazz the expected type
     * @param <T>   the type
     * @return Optional containing the typed value if present and of correct type
     */
    <T> Optional<T> get(String key, Class<T> clazz);

    /**
     * Get the number of entries in the context.
     *
     * @return the size
     */
    int size();

    /**
     * Check if the context is empty.
     *
     * @return true if empty
     */
    boolean isEmpty();

    /**
     * Get the player associated with this context.
     * This is a convenience method for accessing the "player" key.
     *
     * @return Optional containing the player if present
     */
    Optional<Player> getPlayer();

    /**
     * Gets the MenuDefinition class that created this menu.
     * 
     * @return The MenuDefinition class, or empty if not set
     */
    Optional<Class<?>> getMenuDefinitionClass();

    /**
     * Sets the MenuDefinition class that created this menu.
     * 
     * @param clazz The MenuDefinition class
     */
    void setMenuDefinitionClass(Class<?> clazz);
}

