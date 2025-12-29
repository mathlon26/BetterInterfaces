package be.mathijsfollon.betterInterfaces.menu;

import be.mathijsfollon.betterInterfaces.api.MenuItem;
import org.bukkit.inventory.ItemStack;

/**
 * Abstract base class for MenuItem implementations.
 * Can be extended for custom menu item behavior.
 */
public abstract class AbstractMenuItem implements MenuItem {

    protected int slot;
    protected ItemStack itemStack;
    protected boolean movable;

    /**
     * Creates a new MenuItem at the specified slot.
     *
     * @param slot      the slot index
     * @param itemStack the ItemStack to display
     */
    protected AbstractMenuItem(int slot, ItemStack itemStack) {
        this(slot, itemStack, false);
    }

    /**
     * Creates a new MenuItem at the specified slot.
     *
     * @param slot      the slot index
     * @param itemStack the ItemStack to display
     * @param movable   whether the item can be moved
     */
    protected AbstractMenuItem(int slot, ItemStack itemStack, boolean movable) {
        this.slot = slot;
        this.itemStack = itemStack;
        this.movable = movable;
    }

    @Override
    public int getSlot() {
        return slot;
    }

    @Override
    public void setSlot(int slot) {
        this.slot = slot;
    }

    @Override
    public ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public boolean isMovable() {
        return movable;
    }

    @Override
    public void setMovable(boolean movable) {
        this.movable = movable;
    }
}

