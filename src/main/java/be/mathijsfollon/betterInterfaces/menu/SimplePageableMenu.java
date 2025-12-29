package be.mathijsfollon.betterInterfaces.menu;

import be.mathijsfollon.betterInterfaces.api.events.MenuEventManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Simple concrete implementation of AbstractPageableMenu.
 */
public class SimplePageableMenu extends AbstractPageableMenu {
    /**
     * Creates a new SimplePageableMenu.
     *
     * @param title       the menu title
     * @param rows        the number of content rows (navigation row will be added)
     * @param player      the player this menu is for
     * @param eventManager the event manager for firing events
     * @param plugin      the plugin instance
     */
    public SimplePageableMenu(Component title, int rows, Player player, MenuEventManager eventManager, Plugin plugin) {
        super(title, rows, player, eventManager, plugin);
    }
}

