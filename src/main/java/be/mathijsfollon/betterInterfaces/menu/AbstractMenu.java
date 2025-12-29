package be.mathijsfollon.betterInterfaces.menu;

import be.mathijsfollon.betterInterfaces.api.Menu;
import be.mathijsfollon.betterInterfaces.api.MenuItem;
import be.mathijsfollon.betterInterfaces.api.MenuOpenContextStore;
import be.mathijsfollon.betterInterfaces.api.MenuSession;
import be.mathijsfollon.betterInterfaces.api.events.MenuEventManager;
import be.mathijsfollon.betterInterfaces.events.MenuClickEvent;
import be.mathijsfollon.betterInterfaces.events.MenuCloseEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract base class for Menu implementations.
 * Manages an inventory-based menu with items that can be dynamically added/removed.
 * Can be extended for custom menu behavior.
 */
public abstract class AbstractMenu implements Menu, Listener {
    protected final Inventory inventory;
    protected final Map<Integer, MenuItem> items;
    protected final Player player;
    protected final MenuEventManager eventManager;
    protected final Plugin plugin;
    protected MenuSession session;
    protected MenuOpenContextStore context;
    protected boolean open;
    protected boolean uncloseable;

    /**
     * Creates a new AbstractMenu.
     *
     * @param title       the menu title
     * @param size        the inventory size (must be multiple of 9)
     * @param player      the player this menu is for
     * @param eventManager the event manager for firing events
     * @param plugin      the plugin instance
     */
    protected AbstractMenu(Component title, int size, Player player, MenuEventManager eventManager, Plugin plugin) {
        if (size % 9 != 0 || size < 9 || size > 54) {
            throw new IllegalArgumentException("Size must be a multiple of 9 between 9 and 54");
        }
        this.inventory = Bukkit.createInventory(null, size, title);
        this.items = new ConcurrentHashMap<>();
        this.player = player;
        this.eventManager = eventManager;
        this.plugin = plugin;
        this.open = false;
        this.uncloseable = false;
        
        // Register this as a listener for inventory events
        if (plugin != null) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
        }
    }

    @Override
    public void open() {
        if (open || !player.isOnline()) {
            return;
        }

        // Ensure all slots are filled before opening
        ensureFilled();

        player.openInventory(inventory);
        open = true;
        draw();
    }

    /**
     * Ensures all slots are filled. Called before opening the menu.
     * Override this method to customize fill behavior.
     */
    protected void ensureFilled() {
        // By default, do nothing - subclasses can override to fill empty slots
    }

    @Override
    public void close() {
        close(false);
    }

    @Override
    public void close(boolean silently) {
        if (!open) {
            return;
        }

        // If uncloseable and not being closed silently/programmatically, prevent closing
        // Silently = true means it's a programmatic close that should always work
        if (uncloseable && !silently) {
            return;
        }

        // If closing silently, mark as closed first to prevent reopening
        if (silently && uncloseable) {
            open = false;
        }

        if (player.isOnline() && player.getOpenInventory().getTopInventory().equals(inventory)) {
            player.closeInventory();
        }
        
        if (!silently || !uncloseable) {
            open = false;
        }
    }

    @Override
    public boolean isOpen() {
        return open && player.isOnline() && player.getOpenInventory().getTopInventory().equals(inventory);
    }

    /**
     * Draws all menu items to the inventory.
     */
    public void draw() {
        if (!open) {
            return;
        }

        // Clear the inventory first
        inventory.clear();

        // Fill all slots - first with empty air
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, null);
        }

        // Place all menu items at their respective slots
        for (MenuItem item : items.values()) {
            if (item.getSlot() >= 0 && item.getSlot() < inventory.getSize()) {
                inventory.setItem(item.getSlot(), item.getItemStack());
            }
        }
    }

    /**
     * Adds a menu item to this menu.
     *
     * @param item the menu item to add
     */
    public void addItem(MenuItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        
        // If item already exists at this slot, remove it first
        MenuItem existing = items.get(item.getSlot());
        if (existing != null && existing != item) {
            items.remove(item.getSlot());
        }
        
        items.put(item.getSlot(), item);
        
        // If menu is open, update the display
        if (open) {
            inventory.setItem(item.getSlot(), item.getItemStack());
        }
    }

    /**
     * Removes a menu item from this menu.
     *
     * @param slot the slot to remove the item from
     */
    public void removeItem(int slot) {
        MenuItem removed = items.remove(slot);
        if (removed != null && open) {
            inventory.setItem(slot, null);
        }
    }

    /**
     * Gets a menu item at the specified slot.
     *
     * @param slot the slot index
     * @return the menu item, or null if no item at that slot
     */
    public MenuItem getItem(int slot) {
        return items.get(slot);
    }

    /**
     * Gets all menu items in this menu.
     *
     * @return a collection of all menu items
     */
    public Collection<MenuItem> getItems() {
        return Collections.unmodifiableCollection(items.values());
    }

    /**
     * Clears all items from this menu.
     */
    public void clearItems() {
        items.clear();
        if (open) {
            inventory.clear();
        }
    }

    /**
     * Gets the inventory size.
     *
     * @return the inventory size
     */
    public int getSize() {
        return inventory.getSize();
    }

    /**
     * Gets the underlying Bukkit inventory.
     *
     * @return the inventory
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Gets the player this menu is for.
     *
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Only handle clicks in the top inventory (our menu)
        if (!event.getInventory().equals(inventory)) {
            return;
        }

        if (!(event.getWhoClicked() instanceof Player clickedPlayer) || !clickedPlayer.equals(player)) {
            return;
        }

        int slot = event.getSlot();
        
        // Prevent clicking in player inventory
        if (event.getRawSlot() >= inventory.getSize()) {
            return;
        }

        MenuItem menuItem = items.get(slot);
        
        // Check for close/back buttons first
        if (session != null && context != null) {
            if (handleCloseButton(slot)) {
                event.setCancelled(true);
                return;
            }
            if (handleBackButton(slot)) {
                event.setCancelled(true);
                return;
            }
        }
        
        // Fire menu click event if session and context are available
        if (session != null && context != null) {
            MenuClickEvent clickEvent = new MenuClickEvent(
                    player,
                    this,
                    session,
                    context,
                    slot,
                    event.getCurrentItem(),
                    menuItem,
                    event.getClick()
            );
            
            eventManager.fireEvent(clickEvent);

            // Handle cancellation and item movement
            // Always cancel if MenuClickEvent was cancelled, or if item is not movable
            // Also cancel ALL clicks if menu is uncloseable (like confirmation menus)
            if (clickEvent.isCancelled() || (menuItem != null && !menuItem.isMovable()) || uncloseable) {
                event.setCancelled(true);
            }

            // Update item display after click to prevent item movement
            // Do this for all menu items, or if menu is uncloseable (to prevent item theft)
            if (open && (menuItem != null || uncloseable)) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    if (player.isOnline() && player.getOpenInventory().getTopInventory().equals(inventory)) {
                        if (menuItem != null) {
                            inventory.setItem(slot, menuItem.getItemStack());
                        } else {
                            // If no menu item but menu is uncloseable, restore the slot (prevent item removal)
                            inventory.setItem(slot, null);
                        }
                        player.updateInventory();
                    }
                });
            }
        } else {
            // If session/context not set, default behavior: prevent movement of non-movable items
            if (menuItem != null && !menuItem.isMovable()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getInventory().equals(inventory)) {
            return;
        }

        if (!event.getPlayer().equals(player)) {
            return;
        }

        // Only handle if we think the menu is still open
        if (!open) {
            return;
        }

        // If menu is uncloseable and we're not marked as closed, reopen it
        if (uncloseable) {
            // Schedule reopening on next tick
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (player.isOnline() && session != null && context != null && open) {
                    // Fire close event (but it won't actually close since we're reopening)
                    MenuCloseEvent closeEvent = new MenuCloseEvent(player, this, session, context, false);
                    eventManager.fireEvent(closeEvent);
                    
                    // Only reopen if the event wasn't cancelled
                    if (!closeEvent.isCancelled()) {
                        player.openInventory(inventory);
                        open = true;
                    }
                }
            });
            return;
        }

        // Mark as closed and fire close event
        open = false;
        
        // Fire close event if we have session and context
        if (session != null && context != null) {
            MenuCloseEvent closeEvent = new MenuCloseEvent(player, this, session, context, false);
            eventManager.fireEvent(closeEvent);
        }
    }

    /**
     * Sets the session and context for event handling.
     * This should be called when the menu is created.
     *
     * @param session the menu session
     * @param context the menu open context
     */
    public void setSessionAndContext(MenuSession session, MenuOpenContextStore context) {
        this.session = session;
        this.context = context;
    }

    /**
     * Gets the session for events.
     *
     * @return the menu session
     */
    protected MenuSession getSession() {
        return session;
    }

    /**
     * Gets the context for events.
     *
     * @return the menu open context
     */
    private MenuOpenContextStore getContext() {
        return context;
    }

    /**
     * Sets whether the menu can be closed by the player.
     * If set to true, the menu will automatically reopen if the player tries to close it.
     *
     * @param uncloseable true to prevent the player from closing the menu
     */
    public void setUncloseable(boolean uncloseable) {
        this.uncloseable = uncloseable;
    }

    /**
     * Checks if the menu is uncloseable by the player.
     *
     * @return true if the menu cannot be closed by the player
     */
    public boolean isUncloseable() {
        return uncloseable;
    }

    /**
     * Fills all empty slots with glass panes (no name, just decoration).
     * This is useful for creating a cleaner menu appearance.
     */
    public void fillEmptySlots() {
        fillEmptySlots(Material.GRAY_STAINED_GLASS_PANE);
    }

    /**
     * Fills all empty slots with the specified material.
     *
     * @param material the material to fill with
     */
    public void fillEmptySlots(Material material) {
        // Fill all empty slots
        for (int i = 0; i < inventory.getSize(); i++) {
            if (!items.containsKey(i)) {
                ItemStack fillItem = new ItemStack(material);
                fillItem.editMeta(meta -> {
                    meta.displayName(Component.empty()); // No name
                });
                SimpleMenuItem fillMenuItem = new SimpleMenuItem(i, fillItem, false);
                addItem(fillMenuItem);
            }
        }
    }

    /**
     * Fills empty slots with a gradient of glass panes.
     * Creates a visual gradient effect from top to bottom.
     *
     * @param startColor the starting glass pane color (top)
     * @param endColor the ending glass pane color (bottom)
     */
    public void fillGradient(Material startColor, Material endColor) {
        List<Material> gradientColors = getGradientColors(startColor, endColor);
        int totalSlots = inventory.getSize();
        int rows = totalSlots / 9;

        // Collect empty slots grouped by row (top to bottom)
        List<List<Integer>> emptySlotsByRow = new ArrayList<>();
        for (int row = 0; row < rows; row++) {
            emptySlotsByRow.add(new ArrayList<>());
        }

        for (int i = 0; i < totalSlots; i++) {
            if (!items.containsKey(i)) {
                int row = i / 9;
                emptySlotsByRow.get(row).add(i);
            }
        }

        // Find rows that have empty slots
        List<Integer> rowsWithEmptySlots = new ArrayList<>();
        for (int row = 0; row < rows; row++) {
            if (!emptySlotsByRow.get(row).isEmpty()) {
                rowsWithEmptySlots.add(row);
            }
        }

        if (rowsWithEmptySlots.isEmpty()) {
            return;
        }

        // Fill slots with gradient from top to bottom
        for (int rowIndex = 0; rowIndex < rowsWithEmptySlots.size(); rowIndex++) {
            int row = rowsWithEmptySlots.get(rowIndex);
            int colorIndex;
            if (rowsWithEmptySlots.size() == 1) {
                colorIndex = 0;
            } else {
                colorIndex = (rowIndex * (gradientColors.size() - 1)) / (rowsWithEmptySlots.size() - 1);
            }
            Material color = gradientColors.get(Math.min(colorIndex, gradientColors.size() - 1));

            // Fill all empty slots in this row with the same color (non-movable)
            for (int slot : emptySlotsByRow.get(row)) {
                ItemStack fillItem = new ItemStack(color);
                fillItem.editMeta(meta -> {
                    meta.displayName(Component.empty());
                });
                SimpleMenuItem fillMenuItem = new SimpleMenuItem(slot, fillItem, false); // false = not movable
                addItem(fillMenuItem);
            }
        }
    }

    /**
     * Gets a list of glass pane materials forming a gradient between two colors.
     *
     * @param start the start color
     * @param end the end color
     * @return list of materials forming the gradient
     */
    private List<Material> getGradientColors(Material start, Material end) {
        // Common glass pane colors for gradients
        List<Material> colors = Arrays.asList(
            Material.WHITE_STAINED_GLASS_PANE,
            Material.LIGHT_GRAY_STAINED_GLASS_PANE,
            Material.GRAY_STAINED_GLASS_PANE,
            Material.BLACK_STAINED_GLASS_PANE,
            Material.BROWN_STAINED_GLASS_PANE,
            Material.RED_STAINED_GLASS_PANE,
            Material.ORANGE_STAINED_GLASS_PANE,
            Material.YELLOW_STAINED_GLASS_PANE,
            Material.LIME_STAINED_GLASS_PANE,
            Material.GREEN_STAINED_GLASS_PANE,
            Material.CYAN_STAINED_GLASS_PANE,
            Material.LIGHT_BLUE_STAINED_GLASS_PANE,
            Material.BLUE_STAINED_GLASS_PANE,
            Material.PURPLE_STAINED_GLASS_PANE,
            Material.MAGENTA_STAINED_GLASS_PANE,
            Material.PINK_STAINED_GLASS_PANE
        );

        int startIndex = colors.indexOf(start);
        int endIndex = colors.indexOf(end);

        if (startIndex == -1) startIndex = 0;
        if (endIndex == -1) endIndex = colors.size() - 1;

        if (startIndex > endIndex) {
            int temp = startIndex;
            startIndex = endIndex;
            endIndex = temp;
        }

        return colors.subList(startIndex, endIndex + 1);
    }

    /**
     * Adds a close button at the specified slot.
     * Clicking this button will close the menu.
     *
     * @param slot the slot to place the close button
     * @return the created MenuItem for the close button
     */
    public MenuItem addCloseButton(int slot) {
        return addCloseButton(slot, Material.BARRIER);
    }

    /**
     * Adds a close button at the specified slot with custom material.
     *
     * @param slot the slot to place the close button
     * @param material the material for the close button
     * @return the created MenuItem for the close button
     */
    public MenuItem addCloseButton(int slot, Material material) {
        ItemStack closeItem = new ItemStack(material);
        closeItem.editMeta(meta -> {
            meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize("§cClose"));
            meta.lore(List.of(LegacyComponentSerializer.legacyAmpersand().deserialize("§7Click to close this menu")));
        });

        SimpleMenuItem menuItem = new SimpleMenuItem(slot, closeItem, false);
        addItem(menuItem);

        // Store close button slot in context for handleCloseButton to work
        if (context != null) {
            context.put("close-button-slot", slot);
        }

        return menuItem;
    }

    /**
     * Adds a back button at the specified slot.
     * Clicking this button will navigate to the previous menu if one exists.
     *
     * @param slot the slot to place the back button
     * @return the created MenuItem for the back button, or null if no previous menu exists
     */
    public MenuItem addBackButton(int slot) {
        return addBackButton(slot, this.context);
    }
    
    /**
     * Adds a back button at the specified slot with a context to check.
     * This overload allows checking a context passed as parameter (e.g., from onCreate()).
     *
     * @param slot the slot to place the back button
     * @param ctx the context to check for previous session (can be null to use this.context)
     * @return the created MenuItem for the back button, or null if no previous menu exists
     */
    public MenuItem addBackButton(int slot, MenuOpenContextStore ctx) {
        // Use provided context or fall back to this.context
        MenuOpenContextStore contextToCheck = ctx != null ? ctx : this.context;
        
        // Check if there's a previous session (either in current session or context)
        boolean hasPrevious = false;
        if (session != null && session.getPreviousSession().isPresent()) {
            hasPrevious = true;
        } else if (contextToCheck != null && contextToCheck.contains("previous-session")) {
            hasPrevious = true;
        }
        
        if (!hasPrevious) {
            // No previous session available
            return null;
        }

        ItemStack backItem = new ItemStack(Material.OAK_DOOR);
        backItem.editMeta(meta -> {
            meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize("§eBack"));
            meta.lore(List.of(LegacyComponentSerializer.legacyAmpersand().deserialize("§7Click to go back")));
        });

        SimpleMenuItem menuItem = new SimpleMenuItem(slot, backItem, false);
        addItem(menuItem);

        // Store back button slot in context (use this.context if available, otherwise the provided context)
        MenuOpenContextStore contextToStore = this.context != null ? this.context : contextToCheck;
        if (contextToStore != null) {
            contextToStore.put("back-button-slot", slot);
        }

        return menuItem;
    }

    /**
     * Checks if a slot contains a close button and handles the close action.
     *
     * @param slot the slot to check
     * @return true if the slot was a close button and was handled
     */
    protected boolean handleCloseButton(int slot) {
        Integer closeSlot = context.get("close-button-slot", Integer.class).orElse(null);
        if (closeSlot != null && closeSlot == slot && session != null) {
            session.close(true);
            return true;
        }
        return false;
    }

    /**
     * Checks if a slot contains a back button and handles the back action.
     *
     * @param slot the slot to check
     * @return true if the slot was a back button and was handled
     */
    protected boolean handleBackButton(int slot) {
        if (context == null) {
            return false;
        }
        
        Integer backSlot = context.get("back-button-slot", Integer.class).orElse(null);
        if (backSlot == null || backSlot != slot) {
            return false;
        }
        
        // Try to get previous session from current session first, then from context
        if (session != null) {
            if (session.getPreviousSession().isPresent()) {
                return session.goBack();
            }
        }
        
        // Fallback: get from context and navigate manually
        MenuSession previousSession = context.get("previous-session", MenuSession.class).orElse(null);
        if (previousSession != null && session != null) {
            session.close(true);
            previousSession.open();
            return true;
        }
        
        return false;
    }
}

