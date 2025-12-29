package be.mathijsfollon.betterInterfaces.api;

import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Interface for pageable menus that can display multiple pages with navigation.
 * Extends Menu to provide page navigation functionality.
 */
public interface PageableMenu extends Menu {
    /**
     * Adds a page (MenuDefinition) to this pageable menu.
     *
     * @param page the menu definition to add as a page
     * @throws IllegalArgumentException if page is null
     */
    void addPage(MenuDefinition page);

    /**
     * Adds multiple pages to this pageable menu.
     *
     * @param pages the menu definitions to add as pages
     */
    void addPages(List<MenuDefinition> pages);

    /**
     * Sets the navigation items for the bottom row.
     *
     * @param previousItem the item for previous page (can be null)
     * @param nextItem     the item for next page (can be null)
     * @param closeItem    the item for closing (can be null)
     */
    void setNavigationItems(MenuItem previousItem, MenuItem nextItem, MenuItem closeItem);

    /**
     * Sets the navigation items using ItemStacks.
     *
     * @param previousStack the item stack for previous page (can be null)
     * @param nextStack     the item stack for next page (can be null)
     * @param closeStack    the item stack for closing (can be null)
     */
    void setNavigationItems(ItemStack previousStack, ItemStack nextStack, ItemStack closeStack);

    /**
     * Gets the current page index.
     *
     * @return the current page index (0-based)
     */
    int getCurrentPage();

    /**
     * Gets the total number of pages.
     *
     * @return the number of pages
     */
    int getPageCount();

    /**
     * Navigates to a specific page.
     *
     * @param pageIndex the page index to navigate to (0-based)
     * @throws IllegalArgumentException if page index is out of bounds
     */
    void goToPage(int pageIndex);

    /**
     * Navigates to the next page if available.
     */
    void nextPage();

    /**
     * Navigates to the previous page if available.
     */
    void previousPage();

    /**
     * Checks if there is a next page.
     *
     * @return true if there is a next page
     */
    boolean hasNextPage();

    /**
     * Checks if there is a previous page.
     *
     * @return true if there is a previous page
     */
    boolean hasPreviousPage();
}

