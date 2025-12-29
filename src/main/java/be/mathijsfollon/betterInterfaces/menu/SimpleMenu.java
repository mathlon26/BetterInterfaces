package be.mathijsfollon.betterInterfaces.menu;

import be.mathijsfollon.betterInterfaces.api.events.MenuEventManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Simple concrete implementation of AbstractMenu.
 */
public class SimpleMenu extends AbstractMenu {
    /**
     * Creates a new SimpleMenu.
     *
     * @param title       the menu title
     * @param size        the inventory size (must be multiple of 9)
     * @param player      the player this menu is for
     * @param eventManager the event manager for firing events
     * @param plugin      the plugin instance
     */
    public SimpleMenu(Component title, int size, Player player, MenuEventManager eventManager, Plugin plugin) {
        super(title, size, player, eventManager, plugin);
    }
}

