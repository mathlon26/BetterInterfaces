package be.mathijsfollon.betterInterfaces.menu;

import be.mathijsfollon.betterInterfaces.api.*;
import be.mathijsfollon.betterInterfaces.api.events.MenuEventManager;
import be.mathijsfollon.betterInterfaces.events.MenuClickEvent;
import be.mathijsfollon.betterInterfaces.menu.SimpleMenuItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Abstract base class for pageable menu implementations.
 * Can display multiple pages (MenuDefinitions) with navigation controls (previous, next, close) in the bottom row.
 * Can be extended for custom pageable menu behavior.
 */
public abstract class AbstractPageableMenu extends AbstractMenu implements PageableMenu {
    protected final List<MenuDefinition> pages;
    protected int currentPage;
    protected MenuItem previousItem;
    protected MenuItem nextItem;
    protected MenuItem closeItem;
    protected int contentSize; // Size of content area (excluding navigation row)

    /**
     * Creates a new AbstractPageableMenu.
     *
     * @param title       the menu title
     * @param rows        the number of content rows (navigation row will be added)
     * @param player      the player this menu is for
     * @param eventManager the event manager for firing events
     * @param plugin      the plugin instance
     */
    protected AbstractPageableMenu(Component title, int rows, Player player, MenuEventManager eventManager, Plugin plugin) {
        super(title, calculateTotalSize(rows), player, eventManager, plugin);
        this.pages = new ArrayList<>();
        this.currentPage = 0;
        this.contentSize = rows * 9;
    }

    /**
     * Calculates the total inventory size including navigation row.
     *
     * @param contentRows the number of content rows
     * @return the total size
     */
    private static int calculateTotalSize(int contentRows) {
        int totalRows = contentRows + 1; // Add navigation row
        if (totalRows > 6) {
            throw new IllegalArgumentException("Content rows cannot exceed 5 (max 54 slots with navigation)");
        }
        return totalRows * 9;
    }

    /**
     * Adds a page (MenuDefinition) to this pageable menu.
     *
     * @param page the menu definition to add as a page
     */
    public void addPage(MenuDefinition page) {
        if (page == null) {
            throw new IllegalArgumentException("Page cannot be null");
        }
        pages.add(page);
    }

    /**
     * Adds multiple pages to this pageable menu.
     *
     * @param pages the menu definitions to add as pages
     */
    public void addPages(List<MenuDefinition> pages) {
        for (MenuDefinition page : pages) {
            addPage(page);
        }
    }

    /**
     * Sets the navigation items for the bottom row.
     *
     * @param previousItem the item for previous page (can be null)
     * @param nextItem     the item for next page (can be null)
     * @param closeItem    the item for closing (can be null)
     */
    public void setNavigationItems(MenuItem previousItem, MenuItem nextItem, MenuItem closeItem) {
        this.previousItem = previousItem;
        this.nextItem = nextItem;
        this.closeItem = closeItem;
    }

    /**
     * Sets the navigation items using ItemStacks.
     *
     * @param previousStack the item stack for previous page (can be null)
     * @param nextStack     the item stack for next page (can be null)
     * @param closeStack    the item stack for closing (can be null)
     */
    public void setNavigationItems(ItemStack previousStack, ItemStack nextStack, ItemStack closeStack) {
        this.previousItem = previousStack != null ? new SimpleMenuItem(-1, previousStack, false) : null;
        this.nextItem = nextStack != null ? new SimpleMenuItem(-1, nextStack, false) : null;
        this.closeItem = closeStack != null ? new SimpleMenuItem(-1, closeStack, false) : null;
    }

    /**
     * Gets the current page index.
     *
     * @return the current page index (0-based)
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * Gets the total number of pages.
     *
     * @return the number of pages
     */
    public int getPageCount() {
        return pages.size();
    }

    /**
     * Navigates to a specific page.
     *
     * @param pageIndex the page index to navigate to (0-based)
     */
    public void goToPage(int pageIndex) {
        if (pageIndex < 0 || pageIndex >= pages.size()) {
            throw new IllegalArgumentException("Page index out of bounds: " + pageIndex);
        }
        this.currentPage = pageIndex;
        drawCurrentPage();
    }

    /**
     * Navigates to the next page if available.
     */
    public void nextPage() {
        if (hasNextPage()) {
            goToPage(currentPage + 1);
        }
    }

    /**
     * Navigates to the previous page if available.
     */
    public void previousPage() {
        if (hasPreviousPage()) {
            goToPage(currentPage - 1);
        }
    }

    /**
     * Checks if there is a next page.
     *
     * @return true if there is a next page
     */
    public boolean hasNextPage() {
        return currentPage < pages.size() - 1;
    }

    /**
     * Checks if there is a previous page.
     *
     * @return true if there is a previous page
     */
    public boolean hasPreviousPage() {
        return currentPage > 0;
    }

    /**
     * Draws the current page and navigation row.
     */
    public void drawCurrentPage() {
        // Clear all items first
        clearItems();

        // Draw current page content
        if (!pages.isEmpty() && currentPage < pages.size()) {
            MenuDefinition page = pages.get(currentPage);
            List<ItemStack> items = page.getItemStacks();

            int slot = 0;
            for (ItemStack item : items) {
                if (slot >= contentSize) {
                    break; // Don't overflow into navigation row
                }
                if (item != null) {
                    addItem(new SimpleMenuItem(slot, item, false));
                }
                slot++;
            }
        }

        // Fill empty content slots with gradient
        fillContentAreaGradient();

        // Draw navigation row
        drawNavigationRow();

        // Update display
        draw();
    }

    /**
     * Fills empty content area slots with a gradient from top to bottom.
     */
    private void fillContentAreaGradient() {
        List<Material> gradientColors = Arrays.asList(
            Material.LIGHT_BLUE_STAINED_GLASS_PANE,
            Material.BLUE_STAINED_GLASS_PANE,
            Material.CYAN_STAINED_GLASS_PANE
        );

        int contentRows = contentSize / 9;

        // Collect empty slots grouped by row (top to bottom)
        List<List<Integer>> emptySlotsByRow = new ArrayList<>();
        for (int row = 0; row < contentRows; row++) {
            emptySlotsByRow.add(new ArrayList<>());
        }

        for (int i = 0; i < contentSize; i++) {
            if (!items.containsKey(i)) {
                int row = i / 9;
                emptySlotsByRow.get(row).add(i);
            }
        }

        // Find rows that have empty slots
        List<Integer> rowsWithEmptySlots = new ArrayList<>();
        for (int row = 0; row < contentRows; row++) {
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

            // Fill all empty slots in this row with the same color
            for (int slot : emptySlotsByRow.get(row)) {
                ItemStack fillItem = new ItemStack(color);
                fillItem.editMeta(meta -> {
                    meta.displayName(Component.empty());
                });
                addItem(new SimpleMenuItem(slot, fillItem, false));
            }
        }
    }

    /**
     * Draws the navigation row at the bottom.
     */
    private void drawNavigationRow() {
        int navRowStart = contentSize;

        // Back button (left side, slot 1 from left in navigation row = slot 1) if previous menu exists
        if (session != null && session.getPreviousSession().isPresent()) {
            ItemStack backItem = new ItemStack(Material.OAK_DOOR);
            backItem.editMeta(meta -> {
                meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize("§eBack"));
                meta.lore(List.of(LegacyComponentSerializer.legacyAmpersand().deserialize("§7Click to go back")));
            });
            MenuItem back = new SimpleMenuItem(navRowStart + 1, backItem, false);
            addItem(back);
            context.put("back-button-slot", navRowStart + 1);
        } else if (context != null && context.get("previous-session").isPresent()) {
            // Also check context for previous session (for menus opened before session is set)
            ItemStack backItem = new ItemStack(Material.OAK_DOOR);
            backItem.editMeta(meta -> {
                meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize("§eBack"));
                meta.lore(List.of(LegacyComponentSerializer.legacyAmpersand().deserialize("§7Click to go back")));
            });
            MenuItem back = new SimpleMenuItem(navRowStart + 1, backItem, false);
            addItem(back);
            context.put("back-button-slot", navRowStart + 1);
        }

        // Previous button (left side, slot 3 from left in navigation row = slot 3)
        if (hasPreviousPage() && previousItem != null) {
            MenuItem prev = new SimpleMenuItem(navRowStart + 3, previousItem.getItemStack(), false);
            addItem(prev);
        }

        // Close button (center, slot 4 from left in navigation row = slot 4)
        if (closeItem != null) {
            MenuItem close = new SimpleMenuItem(navRowStart + 4, closeItem.getItemStack(), false);
            addItem(close);
            // Store close button slot in context for handleCloseButton to work
            if (context != null) {
                context.put("close-button-slot", navRowStart + 4);
            }
        }

        // Next button (right side, slot 5 from left in navigation row = slot 5)
        if (hasNextPage() && nextItem != null) {
            MenuItem next = new SimpleMenuItem(navRowStart + 5, nextItem.getItemStack(), false);
            addItem(next);
        }

        // Fill empty navigation row slots with glass panes
        for (int i = navRowStart; i < navRowStart + 9; i++) {
            if (!items.containsKey(i)) {
                ItemStack fillItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                fillItem.editMeta(meta -> {
                    meta.displayName(Component.empty());
                });
                addItem(new SimpleMenuItem(i, fillItem, false));
            }
        }
    }

    @Override
    @EventHandler
    public void onInventoryClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        // Only handle clicks in the top inventory (our menu)
        if (!event.getInventory().equals(getInventory())) {
            return;
        }

        if (!(event.getWhoClicked() instanceof Player clickedPlayer) || !clickedPlayer.equals(getPlayer())) {
            return;
        }

        int slot = event.getSlot();
        
        // Prevent clicking in player inventory
        if (event.getRawSlot() >= getInventory().getSize()) {
            return;
        }

        int navRowStart = contentSize;

        // Check if click is in navigation row first, before firing MenuClickEvent
        if (slot >= navRowStart) {
            // Back button (slot 1)
            if (handleBackButton(slot)) {
                event.setCancelled(true);
                return;
            }

            // Close button (slot 4) - use handleCloseButton for consistency
            if (handleCloseButton(slot)) {
                event.setCancelled(true);
                return;
            }

            // Previous button (slot 3)
            if (slot == navRowStart + 3 && hasPreviousPage()) {
                event.setCancelled(true);
                previousPage();
                return;
            }

            // Next button (slot 5)
            if (slot == navRowStart + 5 && hasNextPage()) {
                event.setCancelled(true);
                nextPage();
                return;
            }
        }

        // For content area clicks, call parent to handle normal clicks and fire events
        MenuItem menuItem = items.get(slot);
        
        // Fire menu click event if session and context are available
        if (session != null && context != null) {
            MenuClickEvent clickEvent = new MenuClickEvent(
                    getPlayer(),
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
            if (clickEvent.isCancelled() || (menuItem != null && !menuItem.isMovable())) {
                event.setCancelled(true);
            }

            // Update item display after click to prevent item movement
            if (open && menuItem != null) {
                org.bukkit.Bukkit.getScheduler().runTask(plugin, () -> {
                    if (getPlayer().isOnline() && getPlayer().getOpenInventory().getTopInventory().equals(getInventory())) {
                        getInventory().setItem(slot, menuItem.getItemStack());
                        getPlayer().updateInventory();
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

    @Override
    public void open() {
        // Draw the current page before opening
        if (!pages.isEmpty()) {
            drawCurrentPage();
        }
        super.open();
    }
}

