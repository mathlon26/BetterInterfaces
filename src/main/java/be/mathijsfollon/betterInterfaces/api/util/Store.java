package be.mathijsfollon.betterInterfaces.api.util;

import java.util.Optional;

public interface Store<K, V> {

    /**
     * Put a value into the store.
     * @param key The key
     * @param value The value
     */
    void put(K key, V value);

    /**
     * Get a value from the store.
     * @param key The key
     * @return Optional containing the value if present
     */
    Optional<V> get(K key);

    /**
     * Remove a value from the store.
     * @param key The key
     * @return Optional containing removed value if present
     */
    Optional<V> remove(K key);

    /**
     * Check if the store contains a key.
     * @param key The key
     * @return true if present
     */
    boolean contains(K key);

    /**
     * Clear all entries.
     */
    void clear();
}
