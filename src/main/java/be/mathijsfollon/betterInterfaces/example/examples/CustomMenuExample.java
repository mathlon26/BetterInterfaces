package be.mathijsfollon.betterInterfaces.example.examples;

import be.mathijsfollon.betterInterfaces.api.Menu;
import be.mathijsfollon.betterInterfaces.api.MenuItem;
import be.mathijsfollon.betterInterfaces.api.MenuOpenContextStore;
import be.mathijsfollon.betterInterfaces.api.MenuService;
import be.mathijsfollon.betterInterfaces.api.events.MenuEventManager;
import be.mathijsfollon.betterInterfaces.api.events.MenuEventHandler;
import be.mathijsfollon.betterInterfaces.events.MenuClickEvent;
import be.mathijsfollon.betterInterfaces.events.MenuOpenEvent;
import be.mathijsfollon.betterInterfaces.menu.AbstractMenu;
import be.mathijsfollon.betterInterfaces.menu.AbstractMenuDefinition;
import be.mathijsfollon.betterInterfaces.menu.AbstractMenuItem;
import be.mathijsfollon.betterInterfaces.api.MenuOpenContextStore;
import be.mathijsfollon.betterInterfaces.menu.MenuOpenContext;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 * Custom menu example demonstrating:
 * - Extending AbstractMenuItem for custom item behavior
 * - Extending AbstractMenu for custom menu behavior
 * - Creating specialized menu implementations
 * 
 * This example creates a custom shop item that tracks purchases
 * and a custom shop menu with purchase history.
 */
public class CustomMenuExample {

    /**
     * Custom menu item that tracks purchases.
     * Extends AbstractMenuItem to add custom functionality.
     */
    public static class PurchaseableMenuItem extends AbstractMenuItem {
        private final int price;
        private int purchaseCount;

        /**
         * Creates a purchasable menu item.
         * 
         * @param slot The slot where this item is placed
         * @param itemStack The item stack to display
         * @param price The price to purchase this item
         */
        public PurchaseableMenuItem(int slot, ItemStack itemStack, int price) {
            super(slot, itemStack, false); // Never movable
            this.price = price;
            this.purchaseCount = 0;
            
            // Update item lore to show price
            updateDisplay();
        }

        /**
         * Updates the item display with current price and purchase count.
         */
        private void updateDisplay() {
            ItemStack stack = getItemStack();
            stack.editMeta(meta -> {
                meta.lore(java.util.List.of(
                    Component.text("Price: " + price + " coins"),
                    Component.text("Purchased: " + purchaseCount + " times"),
                    Component.text("Click to purchase")
                ));
            });
        }

        /**
         * Handles a purchase attempt.
         * 
         * @param player The player attempting to purchase
         * @return true if purchase was successful
         */
        public boolean purchase(Player player) {
            // In real implementation, check player's balance
            // For example: if (economy.has(player, price)) { ... }
            
            // Simulate purchase
            purchaseCount++;
            updateDisplay();
            player.sendMessage("You purchased " + getItemStack().getType() + " for " + price + " coins!");
            
            // Give item to player
            player.getInventory().addItem(new ItemStack(getItemStack().getType()));
            
            return true;
        }

        /**
         * Gets the price of this item.
         * 
         * @return The price
         */
        public int getPrice() {
            return price;
        }

        /**
         * Gets the number of times this item has been purchased.
         * 
         * @return Purchase count
         */
        public int getPurchaseCount() {
            return purchaseCount;
        }
    }

    /**
     * Custom shop menu that extends AbstractMenu.
     * Provides specialized shop functionality.
     */
    public static class ShopMenu extends AbstractMenu {
        
        /**
         * Creates a new shop menu.
         * 
         * @param title The menu title
         * @param size The menu size
         * @param player The player viewing the menu
         * @param eventManager The event manager
         * @param plugin The plugin instance
         */
        public ShopMenu(Component title, int size, Player player, 
                       MenuEventManager eventManager, 
                       Plugin plugin) {
            super(title, size, player, eventManager, plugin);
        }

        /**
         * Adds a purchasable item to the shop.
         * 
         * @param slot The slot to place the item
         * @param item The item stack
         * @param price The price
         */
        public void addShopItem(int slot, ItemStack item, int price) {
            PurchaseableMenuItem shopItem = new PurchaseableMenuItem(slot, item, price);
            addItem(shopItem);
        }

        /**
         * Gets a purchasable item at the specified slot.
         * 
         * @param slot The slot
         * @return The purchasable item, or null if not a purchasable item
         */
        public PurchaseableMenuItem getShopItem(int slot) {
            MenuItem item = getItem(slot);
            if (item instanceof PurchaseableMenuItem) {
                return (PurchaseableMenuItem) item;
            }
            return null;
        }

        /**
         * Handles shop item clicks.
         * Override the click handler to handle purchases.
         */
        @org.bukkit.event.EventHandler
        @Override
        public void onInventoryClick(org.bukkit.event.inventory.InventoryClickEvent event) {
            // Check if this is a shop item click first, before calling parent
            if (event.getInventory().equals(inventory) && 
                event.getWhoClicked() instanceof Player clickedPlayer && 
                clickedPlayer.equals(player)) {
                
                int slot = event.getSlot();
                PurchaseableMenuItem shopItem = getShopItem(slot);
                
                if (shopItem != null) {
                    event.setCancelled(true);
                    
                    // Handle purchase
                    shopItem.purchase(clickedPlayer);
                    
                    // Update display
                    draw();
                    return; // Don't call parent, we handled it
                }
            }
            
            // For non-shop items, call parent to handle normally
            super.onInventoryClick(event);
        }
    }

    /**
     * Example menu definition using the custom shop menu.
     */
    public static class CustomShopMenuDefinition extends AbstractMenuDefinition {
        
        public CustomShopMenuDefinition() {
            super("custom-menu", "Custom Shop", 3);
        }

        @Override
        public Menu create(
                MenuOpenContextStore ctx,
                CompletableFuture<MenuOpenEvent> sessionFuture) {
            
            Plugin plugin = ctx.get("plugin", Plugin.class).orElseThrow();
            Player player = ctx.getPlayer().orElseThrow();
            MenuService service = 
                ctx.get("menu-service", MenuService.class).orElseThrow();

            // Create custom shop menu
            ShopMenu shopMenu = new ShopMenu(
                Component.text("Custom Shop"),
                27,
                player,
                service.getEventManager(),
                plugin
            );

            // Add shop items
            shopMenu.addShopItem(10, new ItemStack(Material.DIAMOND), 100);
            shopMenu.addShopItem(12, new ItemStack(Material.EMERALD), 50);
            shopMenu.addShopItem(14, new ItemStack(Material.GOLD_INGOT), 25);
            shopMenu.addShopItem(16, new ItemStack(Material.IRON_INGOT), 10);

            // Fill empty slots with gradient for visual appeal (before adding back button)
            shopMenu.fillGradient(Material.PURPLE_STAINED_GLASS_PANE, Material.MAGENTA_STAINED_GLASS_PANE);
            
            // Add back button if there's a previous menu (after fill so it's on top)
            // Use slot 22 (bottom center) to ensure visibility
            // Pass ctx parameter so it can check for previous session even before this.context is set
            shopMenu.addBackButton(22, ctx);

            return shopMenu;
        }

        @Override
        public Menu onCreate(Menu menu, MenuOpenContextStore ctx) {
            return menu;
        }

        @Override
        public List<ItemStack> getItemStacks() {
            return List.of();
        }
    }

    /**
     * Example event handler for custom menu.
     */
    public static class CustomShopEventHandler {
        
        @MenuEventHandler
        public void onShopMenuClick(MenuClickEvent event) {
            // Additional click handling if needed
            // Purchase is already handled in ShopMenu.onInventoryClick
        }
    }
}

