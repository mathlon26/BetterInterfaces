package be.mathijsfollon.betterInterfaces.example.examples;

import be.mathijsfollon.betterInterfaces.api.Menu;
import be.mathijsfollon.betterInterfaces.api.MenuDefinition;
import be.mathijsfollon.betterInterfaces.api.MenuOpenContextStore;
import be.mathijsfollon.betterInterfaces.api.MenuService;
import be.mathijsfollon.betterInterfaces.api.events.MenuEventHandler;
import be.mathijsfollon.betterInterfaces.events.MenuClickEvent;
import be.mathijsfollon.betterInterfaces.events.MenuOpenEvent;
import be.mathijsfollon.betterInterfaces.menu.AbstractMenuDefinition;
import be.mathijsfollon.betterInterfaces.api.MenuOpenContextStore;
import be.mathijsfollon.betterInterfaces.menu.MenuOpenContext;
import be.mathijsfollon.betterInterfaces.menu.SimplePageableMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Pageable menu example demonstrating:
 * - Creating multi-page menus
 * - Adding pages using MenuDefinitions
 * - Setting up navigation items (previous, next, close)
 * - Automatic page navigation
 * 
 * This example creates a shop menu with multiple pages of items.
 */
public class PageableMenuExample extends AbstractMenuDefinition {
    
    /**
     * Creates a new PageableMenuExample menu definition.
     */
    public PageableMenuExample() {
        super("pageable-menu", "Shop Menu", 4); // 4 content rows + 1 navigation row = 5 rows total
    }

    /**
     * Creates the pageable menu with multiple pages.
     * 
     * @param ctx The menu open context
     * @param sessionFuture Future that will be completed when menu opens
     * @return The created pageable menu
     */
    @Override
    public Menu create(MenuOpenContextStore ctx, CompletableFuture<MenuOpenEvent> sessionFuture) {
        Plugin plugin = ctx.get("plugin", Plugin.class).orElseThrow(
            () -> new IllegalStateException("Plugin must be set in context")
        );
        
        Player player = ctx.getPlayer().orElseThrow(
            () -> new IllegalStateException("Player must be set in context")
        );

        MenuService service = ctx.get("menu-service", MenuService.class).orElseThrow(
            () -> new IllegalStateException("MenuService must be set in context")
        );

        // Create a pageable menu with 4 content rows (navigation row added automatically)
        SimplePageableMenu pageableMenu = new SimplePageableMenu(
            Component.text("Shop Menu"),
            4, // 4 content rows
            player,
            service.getEventManager(),
            plugin
        );

        // Create multiple pages (each page is a MenuDefinition)
        List<MenuDefinition> pages = createShopPages();
        
        // Add all pages to the pageable menu
        pageableMenu.addPages(pages);

        // Set navigation items
        // Previous button (arrow pointing left)
        ItemStack previousButton = new ItemStack(Material.ARROW);
        previousButton.editMeta(meta -> {
            meta.displayName(Component.text("Previous Page"));
        });

        // Next button (arrow pointing right)
        ItemStack nextButton = new ItemStack(Material.ARROW);
        nextButton.editMeta(meta -> {
            meta.displayName(Component.text("Next Page"));
        });

        // Close button (barrier)
        ItemStack closeButton = new ItemStack(Material.BARRIER);
        closeButton.editMeta(meta -> {
            meta.displayName(Component.text("Close"));
        });

        // Set the navigation items
        pageableMenu.setNavigationItems(previousButton, nextButton, closeButton);

        // Fill empty content slots with default glass panes
        // Note: Navigation row will be handled separately
        // We'll need to fill after pages are drawn, but for now we'll let pages handle their own fills

        return pageableMenu;
    }

    /**
     * Creates multiple shop pages as MenuDefinitions.
     * Each page represents a different category of items.
     * 
     * @return List of MenuDefinitions representing pages
     */
    private List<MenuDefinition> createShopPages() {
        List<MenuDefinition> pages = new ArrayList<>();

        // Page 1: Tools
        pages.add(createToolPage());

        // Page 2: Building Blocks
        pages.add(createBuildingBlocksPage());

        // Page 3: Food
        pages.add(createFoodPage());

        // Page 4: Special Items
        pages.add(createSpecialItemsPage());

        return pages;
    }

    /**
     * Creates a page definition for tools.
     * 
     * @return MenuDefinition for tools page
     */
    private MenuDefinition createToolPage() {
        return new AbstractMenuDefinition("tool-page", "Tools", 4) {
            @Override
            public List<ItemStack> getItemStacks() {
                List<ItemStack> items = new ArrayList<>();
                
                // Fill with tools (36 slots for 4 rows)
                items.add(createShopItem(Material.WOODEN_PICKAXE, 10));
                items.add(createShopItem(Material.STONE_PICKAXE, 20));
                items.add(createShopItem(Material.IRON_PICKAXE, 50));
                items.add(createShopItem(Material.DIAMOND_PICKAXE, 100));
                items.add(createShopItem(Material.WOODEN_AXE, 10));
                items.add(createShopItem(Material.STONE_AXE, 20));
                items.add(createShopItem(Material.IRON_AXE, 50));
                items.add(createShopItem(Material.DIAMOND_AXE, 100));
                
                // Fill remaining slots with null or empty spaces
                while (items.size() < 36) {
                    items.add(null);
                }
                
                return items;
            }

            @Override
            public Menu onCreate(Menu menu, MenuOpenContextStore ctx) {
                return menu;
            }

            @Override
            public Menu create(MenuOpenContextStore ctx, CompletableFuture<MenuOpenEvent> sessionFuture) {
                // Not used for pageable menu pages
                return null;
            }
        };
    }

    /**
     * Creates a page definition for building blocks.
     * 
     * @return MenuDefinition for building blocks page
     */
    private MenuDefinition createBuildingBlocksPage() {
        return new AbstractMenuDefinition("building-page", "Building Blocks", 4) {
            @Override
            public List<ItemStack> getItemStacks() {
                List<ItemStack> items = new ArrayList<>();
                
                items.add(createShopItem(Material.STONE, 5));
                items.add(createShopItem(Material.COBBLESTONE, 2));
                items.add(createShopItem(Material.BRICKS, 10));
                items.add(createShopItem(Material.OAK_PLANKS, 3));
                items.add(createShopItem(Material.GLASS, 8));
                items.add(createShopItem(Material.IRON_BLOCK, 100));
                items.add(createShopItem(Material.GOLD_BLOCK, 200));
                items.add(createShopItem(Material.DIAMOND_BLOCK, 500));
                
                while (items.size() < 36) {
                    items.add(null);
                }
                
                return items;
            }

            @Override
            public Menu onCreate(Menu menu, MenuOpenContextStore ctx) {
                return menu;
            }

            @Override
            public Menu create(MenuOpenContextStore ctx, CompletableFuture<MenuOpenEvent> sessionFuture) {
                return null;
            }
        };
    }

    /**
     * Creates a page definition for food items.
     * 
     * @return MenuDefinition for food page
     */
    private MenuDefinition createFoodPage() {
        return new AbstractMenuDefinition("food-page", "Food", 4) {
            @Override
            public List<ItemStack> getItemStacks() {
                List<ItemStack> items = new ArrayList<>();
                
                items.add(createShopItem(Material.BREAD, 5));
                items.add(createShopItem(Material.COOKED_BEEF, 10));
                items.add(createShopItem(Material.COOKED_PORKCHOP, 10));
                items.add(createShopItem(Material.APPLE, 3));
                items.add(createShopItem(Material.GOLDEN_APPLE, 50));
                items.add(createShopItem(Material.CAKE, 20));
                items.add(createShopItem(Material.COOKIE, 2));
                items.add(createShopItem(Material.PUMPKIN_PIE, 8));
                
                while (items.size() < 36) {
                    items.add(null);
                }
                
                return items;
            }

            @Override
            public Menu onCreate(Menu menu, MenuOpenContextStore ctx) {
                return menu;
            }

            @Override
            public Menu create(MenuOpenContextStore ctx, CompletableFuture<MenuOpenEvent> sessionFuture) {
                return null;
            }
        };
    }

    /**
     * Creates a page definition for special items.
     * 
     * @return MenuDefinition for special items page
     */
    private MenuDefinition createSpecialItemsPage() {
        return new AbstractMenuDefinition("special-page", "Special Items", 4) {
            @Override
            public List<ItemStack> getItemStacks() {
                List<ItemStack> items = new ArrayList<>();
                
                items.add(createShopItem(Material.ENDER_PEARL, 100));
                items.add(createShopItem(Material.EXPERIENCE_BOTTLE, 50));
                items.add(createShopItem(Material.FIREWORK_ROCKET, 25));
                items.add(createShopItem(Material.ENCHANTED_BOOK, 200));
                items.add(createShopItem(Material.BEACON, 1000));
                
                while (items.size() < 36) {
                    items.add(null);
                }
                
                return items;
            }

            @Override
            public Menu onCreate(Menu menu, MenuOpenContextStore ctx) {
                return menu;
            }

            @Override
            public Menu create(MenuOpenContextStore ctx, CompletableFuture<MenuOpenEvent> sessionFuture) {
                return null;
            }
        };
    }

    /**
     * Helper method to create a shop item with price in lore.
     * 
     * @param material The material for the item
     * @param price The price of the item
     * @return ItemStack with price information
     */
    private ItemStack createShopItem(Material material, int price) {
        ItemStack item = new ItemStack(material);
        item.editMeta(meta -> {
            meta.lore(List.of(
                Component.text("Price: " + price + " coins"),
                Component.text("Click to purchase")
            ));
        });
        return item;
    }

    /**
     * Handles clicks on shop items in the pageable menu.
     * This handles clicks on items from any of the pages.
     */
    @MenuEventHandler
    public void onShopItemClick(MenuClickEvent event) {
        event.setCancelled(true); // Always prevent item removal
        
        ItemStack clickedItem = event.getItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        // Skip glass panes (used for decoration/filling)
        Material material = clickedItem.getType();
        if (material.name().contains("GLASS_PANE") || material.name().contains("STAINED_GLASS_PANE")) {
            return; // Glass panes are not purchasable
        }

        Player player = event.getPlayer();
        
        // Extract price from lore - only process items that actually have a price
        int price = extractPriceFromLore(clickedItem);
        if (price <= 0) {
            // Item has no price, so it's not a shop item (probably a decorative item)
            return;
        }
        
        String itemName = clickedItem.getType().name();
        
        // Simulate purchase check
        boolean canAfford = simulatePurchaseCheck(player, price);
        
        if (canAfford) {
            // Give the item to the player
            ItemStack itemToGive = clickedItem.clone();
            itemToGive.setAmount(1);
            
            // Clear price lore before giving
            itemToGive.editMeta(meta -> {
                meta.lore(null);
            });
            
            player.getInventory().addItem(itemToGive);
            player.sendMessage("§aPurchased " + itemName + " for " + price + " coins!");
            
            // In a real implementation, you would deduct coins here
            // Example: economy.withdrawPlayer(player, price);
        } else {
            player.sendMessage("§cYou cannot afford " + itemName + " (Cost: " + price + " coins)");
        }
    }
    
    /**
     * Extracts price from item lore.
     * 
     * @param item The item to extract price from
     * @return The price, or 0 if not found
     */
    private int extractPriceFromLore(ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
            List<Component> lore = item.getItemMeta().lore();
            if (lore != null && !lore.isEmpty()) {
                String firstLine = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(lore.get(0));
                // Extract number from "Price: X coins"
                try {
                    String numberPart = firstLine.replaceAll("[^0-9]", "");
                    return Integer.parseInt(numberPart);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        }
        return 0;
    }
    
    /**
     * Simulates a purchase check.
     * In a real implementation, this would check the player's balance.
     * 
     * @param player The player
     * @param price The price to check
     * @return true if player can afford
     */
    private boolean simulatePurchaseCheck(Player player, int price) {
        // Simulate: always return true for demo purposes
        // In real implementation: return economy.getBalance(player) >= price;
        return true;
    }

    @Override
    public Menu onCreate(Menu menu, MenuOpenContextStore ctx) {
        // PageableMenu automatically has back button in navigation row if previous menu exists
        return menu;
    }

    @Override
    public List<ItemStack> getItemStacks() {
        return List.of();
    }
}

