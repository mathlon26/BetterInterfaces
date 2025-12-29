package be.mathijsfollon.betterInterfaces.service;

import be.mathijsfollon.betterInterfaces.api.*;
import be.mathijsfollon.betterInterfaces.api.events.MenuEventManager;
import be.mathijsfollon.betterInterfaces.events.MenuEventManagerImpl;
import be.mathijsfollon.betterInterfaces.api.events.MenuEventListener;
import be.mathijsfollon.betterInterfaces.api.exceptions.MenuNotRegisteredException;
import be.mathijsfollon.betterInterfaces.events.MenuOpenEvent;
import be.mathijsfollon.betterInterfaces.api.MenuOpenContextStore;
import be.mathijsfollon.betterInterfaces.menu.MenuSessionImpl;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class BetterInterfacesMenuService implements MenuService {
    private final MenuDefinitionStore menuStore;
    private final MenuEventManager eventManager;

    public BetterInterfacesMenuService() {
        menuStore = new MenuDefinitionStore();
        eventManager = new MenuEventManagerImpl();
    }

    @Override
    public void registerMenu(MenuDefinition definition) {
        menuStore.put(definition.getId(), definition);
        
        if (definition instanceof MenuEventListener listener) {
            eventManager.registerListener(listener);
        }
    }

    @Override
    public void registerMenus(List<MenuDefinition> definitions) {
        for (MenuDefinition definition : definitions) {
            registerMenu(definition);
        }
    }

    @Override
    public void unregisterMenu(String id) {
        Optional<MenuDefinition> definition = menuStore.remove(id);
        
        definition.ifPresent(def -> {
            if (def instanceof MenuEventListener listener) {
                eventManager.unregisterListener(listener);
            }
        });
    }

    public MenuDefinitionParser getParser() {
        return null;
    }

    @Override
    public MenuSession openMenu(Player player, String id, MenuOpenContextStore ctx) throws MenuNotRegisteredException {
        return openMenu(player, id, ctx, null);
    }

    @Override
    public MenuSession openMenu(Player player, String id, MenuOpenContextStore ctx, MenuSession currentSession) throws MenuNotRegisteredException {
        Optional<MenuDefinition> definition = menuStore.get(id);
        if (definition.isEmpty()) {
            throw new MenuNotRegisteredException(id);
        }

        MenuDefinition menuDef = definition.get();
        ctx.put("player", player);
        ctx.put("menu-service", this); // Add service to context for menu creation
        ctx.setMenuDefinitionClass(menuDef.getClass()); // Store the MenuDefinition class
        
        // Store previous session in context for back button support
        if (currentSession != null) {
            ctx.put("previous-session", currentSession);
        }

        CompletableFuture<MenuOpenEvent> future = new CompletableFuture<>();

        Menu menu = menuDef.create(ctx, future);

        // Create session with previous session for navigation
        MenuSessionImpl session = new MenuSessionImpl(menu, future, player, ctx, eventManager);
        if (currentSession != null) {
            session.setPreviousSession(currentSession);
        }

        session.open();

        return session;
    }

    /**
     * Gets the event manager.
     *
     * @return the event manager
     */
    @Override
    public MenuEventManager getEventManager() {
        return eventManager;
    }
}
