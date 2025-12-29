package be.mathijsfollon.betterInterfaces.events;

import be.mathijsfollon.betterInterfaces.api.Menu;
import be.mathijsfollon.betterInterfaces.api.MenuOpenContextStore;
import be.mathijsfollon.betterInterfaces.api.MenuSession;
import be.mathijsfollon.betterInterfaces.api.events.MenuEvent;
import org.bukkit.entity.Player;

/**
 * Event fired when a menu is closed.
 */
public class MenuCloseEvent implements MenuEvent {
    private final Player player;
    private final Menu menu;
    private final MenuSession session;
    private final MenuOpenContextStore context;
    private final boolean silent;
    private boolean cancelled;

    public MenuCloseEvent(Player player, Menu menu, MenuSession session, MenuOpenContextStore context, boolean silent) {
        this.player = player;
        this.menu = menu;
        this.session = session;
        this.context = context;
        this.silent = silent;
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

    /**
     * Checks if the menu was closed silently.
     *
     * @return true if closed silently, false otherwise
     */
    public boolean isSilent() {
        return silent;
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
