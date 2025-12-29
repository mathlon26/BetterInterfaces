package be.mathijsfollon.betterInterfaces.menu;

import be.mathijsfollon.betterInterfaces.api.Menu;
import be.mathijsfollon.betterInterfaces.api.MenuOpenContextStore;
import be.mathijsfollon.betterInterfaces.api.MenuSession;
import be.mathijsfollon.betterInterfaces.api.events.MenuEventManager;
import be.mathijsfollon.betterInterfaces.events.MenuCloseEvent;
import be.mathijsfollon.betterInterfaces.events.MenuOpenEvent;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class MenuSessionImpl implements MenuSession {
    private final Menu menu;
    private final CompletableFuture<MenuOpenEvent> result;
    private final Player player;
    private final MenuOpenContextStore context;
    private final MenuEventManager eventManager;
    private MenuSession previousSession; // Navigation stack


    public MenuSessionImpl(Menu menu, CompletableFuture<MenuOpenEvent> result, Player player, MenuOpenContextStore context, MenuEventManager eventManager) {
        this.menu = menu;
        this.result = result;
        this.player = player;
        this.context = context;
        this.eventManager = eventManager;
    }

    /**
     * Creates a new MenuSession with a previous session for navigation.
     */
    public MenuSessionImpl(Menu menu, CompletableFuture<MenuOpenEvent> result, Player player, MenuOpenContextStore context, MenuEventManager eventManager, MenuSession previousSession) {
        this(menu, result, player, context, eventManager);
        this.previousSession = previousSession;
    }

    /**
     * Sets the previous session for navigation.
     */
    public void setPreviousSession(MenuSession previousSession) {
        this.previousSession = previousSession;
    }

    @Override
    public boolean isOpen() {
        return menu.isOpen();
    }

    @Override
    public void open() {
        if (isOpen()) return;

        // Set session and context on menu if it's an AbstractMenu
        if (menu instanceof AbstractMenu abstractMenu) {
            abstractMenu.setSessionAndContext(this, context);
        }

        // Fire menu open event
        MenuOpenEvent openEvent = new MenuOpenEvent(player, menu, this, context);
        eventManager.fireEvent(openEvent);

        // If event is cancelled, don't open the menu
        if (openEvent.isCancelled()) {
            return;
        }

        menu.open();
        
        // Complete the result future with the open event
        result.complete(openEvent);
    }

    @Override
    public void close() {
        close(false);
    }

    @Override
    public void close(boolean silently) {
        // Fire menu close event first
        MenuCloseEvent closeEvent = new MenuCloseEvent(player, menu, this, context, silently);
        eventManager.fireEvent(closeEvent);

        // If event is cancelled, don't close the menu
        if (closeEvent.isCancelled()) {
            return;
        }

        // Close the menu (use silently=true for programmatic closes to bypass uncloseable check)
        menu.close(silently);
    }


    @Override
    public CompletableFuture<MenuOpenEvent> getResult() {
        return result;
    }

    @Override
    public MenuEventManager getEventManager() {
        return eventManager;
    }

    @Override
    public Optional<MenuSession> getPreviousSession() {
        return Optional.ofNullable(previousSession);
    }

    @Override
    public boolean goBack() {
        if (previousSession == null) {
            return false;
        }

        // Close current menu silently
        close(true);

        // Open previous menu
        previousSession.open();

        return true;
    }
}
