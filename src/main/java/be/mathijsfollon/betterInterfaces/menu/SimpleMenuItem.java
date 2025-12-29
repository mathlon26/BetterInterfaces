package be.mathijsfollon.betterInterfaces.menu;

import be.mathijsfollon.betterInterfaces.api.MenuItem;
import org.bukkit.inventory.ItemStack;

/**
 * Simple concrete implementation of AbstractMenuItem.
 */
public class SimpleMenuItem extends AbstractMenuItem {
    /**
     * Creates a new SimpleMenuItem at the specified slot.
     *
     * @param slot      the slot index
     * @param itemStack the ItemStack to display
     */
    public SimpleMenuItem(int slot, ItemStack itemStack) {
        super(slot, itemStack);
    }

    /**
     * Creates a new SimpleMenuItem at the specified slot.
     *
     * @param slot      the slot index
     * @param itemStack the ItemStack to display
     * @param movable   whether the item can be moved
     */
    public SimpleMenuItem(int slot, ItemStack itemStack, boolean movable) {
        super(slot, itemStack, movable);
    }
}

