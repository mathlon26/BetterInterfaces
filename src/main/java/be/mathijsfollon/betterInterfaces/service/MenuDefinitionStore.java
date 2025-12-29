package be.mathijsfollon.betterInterfaces.service;

import be.mathijsfollon.betterInterfaces.api.Menu;
import be.mathijsfollon.betterInterfaces.api.MenuDefinition;
import be.mathijsfollon.betterInterfaces.api.util.Store;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MenuDefinitionStore implements Store<String, MenuDefinition> {

    private final Map<String, MenuDefinition> menus;

    public MenuDefinitionStore() {
        this.menus = new ConcurrentHashMap<>();
    }

    @Override
    public void put(String key, MenuDefinition value) {
        menus.put(key, value);
    }

    @Override
    public Optional<MenuDefinition> get(String key) {
        return Optional.ofNullable(menus.get(key));
    }

    @Override
    public Optional<MenuDefinition> remove(String key) {
        return Optional.ofNullable(menus.remove(key));
    }

    @Override
    public boolean contains(String key) {
        return menus.containsKey(key);
    }

    @Override
    public void clear() {
        menus.clear();
    }
}
