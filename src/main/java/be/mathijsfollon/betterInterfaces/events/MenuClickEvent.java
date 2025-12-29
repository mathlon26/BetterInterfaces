package be.mathijsfollon.betterInterfaces.events;

import be.mathijsfollon.betterInterfaces.api.Menu;
import be.mathijsfollon.betterInterfaces.api.MenuItem;
import be.mathijsfollon.betterInterfaces.api.MenuOpenContextStore;
import be.mathijsfollon.betterInterfaces.api.MenuSession;
import be.mathijsfollon.betterInterfaces.api.events.MenuEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * Event fired when a menu item is clicked.
 */
public class MenuClickEvent implements MenuEvent {
    private final Player player;
    private final Menu menu;
    private final MenuSession session;
    private final MenuOpenContextStore context;
    private final int slot;
    private final ItemStack item;
    private final MenuItem menuItem;
    private final ClickType clickType;
    private boolean cancelled;

    public MenuClickEvent(Player player, Menu menu, MenuSession session, MenuOpenContextStore context, int slot, ItemStack item, MenuItem menuItem, ClickType clickType) {
        this.player = player;
        this.menu = menu;
        this.session = session;
        this.context = context;
        this.slot = slot;
        this.item = item;
        this.menuItem = menuItem;
        this.clickType = clickType;
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
     * Gets the slot that was clicked.
     *
     * @return the slot index
     */
    public int getSlot() {
        return slot;
    }

    /**
     * Gets the item stack that was clicked.
     *
     * @return the item stack, or null if no item
     */
    public ItemStack getItem() {
        return item;
    }

    /**
     * Gets the menu item that was clicked.
     *
     * @return the menu item, or null if no menu item at that slot
     */
    public MenuItem getMenuItem() {
        return menuItem;
    }

    /**
     * Gets the type of click that occurred.
     *
     * @return the click type
     */
    public ClickType getClickType() {
        return clickType;
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
