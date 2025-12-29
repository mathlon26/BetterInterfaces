package be.mathijsfollon.betterInterfaces.events;

import be.mathijsfollon.betterInterfaces.api.Menu;
import be.mathijsfollon.betterInterfaces.api.MenuOpenContextStore;
import be.mathijsfollon.betterInterfaces.api.MenuSession;
import be.mathijsfollon.betterInterfaces.api.events.MenuEvent;
import org.bukkit.entity.Player;

/**
 * Event fired when a menu is opened.
 */
public class MenuOpenEvent implements MenuEvent {
    private final Player player;
    private final Menu menu;
    private final MenuSession session;
    private final MenuOpenContextStore context;
    private boolean cancelled;

    public MenuOpenEvent(Player player, Menu menu, MenuSession session, MenuOpenContextStore context) {
        this.player = player;
        this.menu = menu;
        this.session = session;
        this.context = context;
        this.cancelled = false;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public Menu getMenu() {
        return menu;
    }

    @Override
    public MenuSession getSession() {
        return session;
    }

    @Override
    public MenuOpenContextStore getContext() {
        return context;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
