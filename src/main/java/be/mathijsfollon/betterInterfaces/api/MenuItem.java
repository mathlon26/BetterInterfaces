package be.mathijsfollon.betterInterfaces.api;

import org.bukkit.inventory.ItemStack;

/**
 * Represents an item in a menu.
 */
public interface MenuItem {
    /**
     * Gets the slot this item is placed in.
     *
     * @return the slot index
     */
    int getSlot();

    /**
     * Sets the slot this item should be placed in.
     *
     * @param slot the slot index
     */
    void setSlot(int slot);

    /**
     * Gets the ItemStack displayed for this menu item.
     *
     * @return the ItemStack
     */
    ItemStack getItemStack();

    /**
     * Sets the ItemStack displayed for this menu item.
     *
     * @param itemStack the ItemStack to display
     */
    void setItemStack(ItemStack itemStack);

    /**
     * Checks if this item can be moved/taken from the inventory.
     *
     * @return true if the item can be moved
     */
    boolean isMovable();

    /**
     * Sets whether this item can be moved/taken from the inventory.
     *
     * @param movable true if the item should be movable
     */
    void setMovable(boolean movable);
}
