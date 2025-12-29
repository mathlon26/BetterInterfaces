package be.mathijsfollon.betterInterfaces.menu;

import be.mathijsfollon.betterInterfaces.api.MenuOpenContextStore;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of MenuOpenContext.
 * Provides a thread-safe key-value store for passing custom data to menu definitions.
 */
public class MenuOpenContext implements MenuOpenContextStore {
    private final Map<String, Object> data;

    /**
     * Creates a new empty MenuOpenContext.
     */
    public MenuOpenContext() {
        this.data = new ConcurrentHashMap<>();
    }

    /**
     * Put a value into the context.
     *
     * @param key   the key
     * @param value the value
     */
    @Override
    public void put(String key, Object value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        data.put(key, value);
    }

    /**
     * Get a value from the context.
     *
     * @param key the key
     * @return Optional containing the value if present
     */
    @Override
    public Optional<Object> get(String key) {
        return Optional.ofNullable(data.get(key));
    }

    /**
     * Get a typed value from the context.
     *
     * @param key   the key
     * @param clazz the expected type
     * @param <T>   the type
     * @return Optional containing the typed value if present and of correct type
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<T> get(String key, Class<T> clazz) {
        Object value = data.get(key);
        if (value == null) {
            return Optional.empty();
        }
        if (clazz.isInstance(value)) {
            return Optional.of((T) value);
        }
        return Optional.empty();
    }

    /**
     * Remove a value from the context.
     *
     * @param key the key
     * @return Optional containing removed value if present
     */
    @Override
    public Optional<Object> remove(String key) {
        return Optional.ofNullable(data.remove(key));
    }

    /**
     * Check if the context contains a key.
     *
     * @param key the key
     * @return true if present
     */
    @Override
    public boolean contains(String key) {
        return data.containsKey(key);
    }

    /**
     * Clear all entries from the context.
     */
    @Override
    public void clear() {
        data.clear();
    }

    /**
     * Get the number of entries in the context.
     *
     * @return the size
     */
    @Override
    public int size() {
        return data.size();
    }

    /**
     * Check if the context is empty.
     *
     * @return true if empty
     */
    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    /**
     * Get the player associated with this context.
     * This is a convenience method for accessing the "player" key.
     *
     * @return Optional containing the player if present
     */
    @Override
    public Optional<Player> getPlayer() {
        return get("player", Player.class);
    }

    /**
     * Gets the MenuDefinition class that created this menu.
     * 
     * @return The MenuDefinition class, or empty if not set
     */
    @SuppressWarnings("unchecked")
    @Override
    public Optional<Class<?>> getMenuDefinitionClass() {
        Object value = data.get("menu-definition-class");
        if (value instanceof Class) {
            return Optional.of((Class<?>) value);
        }
        return Optional.empty();
    }

    /**
     * Sets the MenuDefinition class that created this menu.
     * 
     * @param clazz The MenuDefinition class
     */
    @Override
    public void setMenuDefinitionClass(Class<?> clazz) {
        put("menu-definition-class", clazz);
    }
}

