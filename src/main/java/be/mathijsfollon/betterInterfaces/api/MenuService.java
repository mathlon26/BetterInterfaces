package be.mathijsfollon.betterInterfaces.api;

import be.mathijsfollon.betterInterfaces.api.events.MenuEventManager;
import be.mathijsfollon.betterInterfaces.api.exceptions.MenuNotRegisteredException;
import org.bukkit.entity.Player;

import java.util.List;

public interface MenuService {
    void registerMenu(MenuDefinition definition);
    void registerMenus(List<MenuDefinition> definitions);
    void unregisterMenu(String id);
    MenuDefinitionParser getParser();
    MenuSession openMenu(Player player, String id, MenuOpenContextStore ctx);

    /**
     * Opens a menu with navigation support (stack-based).
     * The current session will be set as the previous session for the new menu,
     * allowing users to navigate back.
     *
     * @param player the player to open the menu for
     * @param id the menu ID to open
     * @param ctx the menu open context
     * @param currentSession the current menu session (will become previous session)
     * @return the new menu session
     * @throws MenuNotRegisteredException if the menu is not registered
     */
    MenuSession openMenu(Player player, String id, MenuOpenContextStore ctx, MenuSession currentSession) throws MenuNotRegisteredException;

    MenuEventManager getEventManager();
}
