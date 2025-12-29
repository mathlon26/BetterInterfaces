package be.mathijsfollon.betterInterfaces.example.examples;

import be.mathijsfollon.betterInterfaces.api.Menu;
import be.mathijsfollon.betterInterfaces.api.MenuSession;
import be.mathijsfollon.betterInterfaces.api.events.MenuEventHandler;
import be.mathijsfollon.betterInterfaces.events.MenuClickEvent;
import be.mathijsfollon.betterInterfaces.events.MenuCloseEvent;
import be.mathijsfollon.betterInterfaces.events.MenuOpenEvent;
import be.mathijsfollon.betterInterfaces.menu.AbstractMenuDefinition;
import be.mathijsfollon.betterInterfaces.api.MenuOpenContextStore;
import be.mathijsfollon.betterInterfaces.menu.SimpleMenu;
import be.mathijsfollon.betterInterfaces.menu.SimpleMenuItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Basic menu example demonstrating:
 * - Creating a simple menu with items
 * - Handling click events
 * - Using MenuOpenContext to pass data
 * - Event lifecycle (open, click, close)
 * 
 * This example creates a welcome menu that opens when a player joins.
 */
public class BasicMenuExample extends AbstractMenuDefinition {
    
    /**
     * Creates a new BasicMenuExample menu definition.
     */
    public BasicMenuExample() {
        super("basic-menu", "Welcome Menu", 3); // 3 rows = 27 slots
    }

    /**
     * Configures the menu after creation.
     * This is called automatically by the default create() method.
     * 
     * @param menu The menu instance to configure
     * @param ctx The context containing data passed when opening the menu
     * @return The configured menu
     */
    @Override
    public Menu onCreate(Menu menu, MenuOpenContextStore ctx) {
        // Cast to SimpleMenu to add items
        if (!(menu instanceof SimpleMenu simpleMenu)) {
            return menu;
        }

        // Add items to the menu dynamically
        // Slot 10 (second row, second column)
        ItemStack welcomeItem = new ItemStack(Material.BOOK);
        welcomeItem.editMeta(meta -> {
            meta.displayName(Component.text("Welcome!"));
            meta.lore(List.of(Component.text("Click for welcome message")));
        });
        simpleMenu.addItem(new SimpleMenuItem(10, welcomeItem));

        // Slot 13 (middle of second row)
        ItemStack rewardItem = new ItemStack(Material.GOLD_INGOT);
        rewardItem.editMeta(meta -> {
            meta.displayName(Component.text("Reward"));
            meta.lore(List.of(Component.text("Click to receive reward")));
        });
        simpleMenu.addItem(new SimpleMenuItem(13, rewardItem));

        // Slot 16 (second row, last column)
        ItemStack infoItem = new ItemStack(Material.EMERALD);
        infoItem.editMeta(meta -> {
            meta.displayName(Component.text("Info"));
            meta.lore(List.of(Component.text("Click for information")));
        });
        simpleMenu.addItem(new SimpleMenuItem(16, infoItem));

        // Fill empty slots with gradient for visual appeal (before adding back button)
        simpleMenu.fillGradient(Material.BLUE_STAINED_GLASS_PANE, Material.CYAN_STAINED_GLASS_PANE);
        
        // Add back button if there's a previous menu (after fill so it's on top)
        // Use slot 22 (bottom center) to ensure visibility
        // Pass ctx parameter so it can check for previous session even before this.context is set
        simpleMenu.addBackButton(22, ctx);

        return menu;
    }

    /**
     * Handles menu click events.
     * This method is automatically called when a player clicks an item in this menu.
     * The @MenuEventHandler annotation ensures it only handles events for this menu.
     * 
     * @param event The click event containing information about the click
     */
    @MenuEventHandler
    public void onMenuClick(MenuClickEvent event) {
        ItemStack clickedItem = event.getItem();
        if (clickedItem == null) {
            return;
        }

        Player player = event.getPlayer();
        Material clickedMaterial = clickedItem.getType();

        // Handle different item clicks
        switch (clickedMaterial) {
            case GOLD_INGOT:
                // Give player a reward with animation
                ItemStack reward = new ItemStack(Material.GOLD_INGOT, 5);
                player.getInventory().addItem(reward);
                player.sendMessage("§aYou received §65 gold ingots§a!");
                player.sendMessage("§7This demonstrates item interaction in menus.");
                event.setCancelled(true); // Prevent item removal
                break;

            case BOOK:
                // Send welcome message with formatting
                player.sendMessage("§6=====================================");
                player.sendMessage("§eWelcome to the server!");
                player.sendMessage("§7This menu demonstrates basic menu functionality.");
                player.sendMessage("§6=====================================");
                event.setCancelled(true);
                break;

            case EMERALD:
                // Show detailed info
                player.sendMessage("§bBetterInterfaces Menu System");
                player.sendMessage("§7- Dynamic menu creation");
                player.sendMessage("§7- Event-based interactions");
                player.sendMessage("§7- Easy to extend and customize");
                event.setCancelled(true);
                break;

            default:
                // By default, prevent taking items from the menu
                event.setCancelled(true);
                break;
        }
    }

    /**
     * Handles menu open events.
     * Called when the menu is opened.
     * 
     * @param event The open event
     */
    @MenuEventHandler
    public void onMenuOpen(MenuOpenEvent event) {
        Player player = event.getPlayer();
        
        // Get custom data from context
        event.getContext().get("custom-message", String.class).ifPresent(message -> {
            player.sendMessage(message);
        });

        // Log menu opening (example)
        System.out.println("Menu opened for player: " + player.getName());
    }

    /**
     * Handles menu close events.
     * Called when the menu is closed.
     * 
     * @param event The close event
     */
    @MenuEventHandler
    public void onMenuClose(MenuCloseEvent event) {
        Player player = event.getPlayer();
        
        if (event.isSilent()) {
            // Menu was closed silently (programmatically)
            System.out.println("Menu closed silently for: " + player.getName());
        } else {
            // Player closed the menu
            System.out.println("Menu closed by player: " + player.getName());
        }
    }

    /**
     * Configures the menu after creation.
     * This is called automatically by the default create() method.
     * The default create() method creates a SimpleMenu and calls this method.
     */

    /**
     * Returns the item stacks for this menu definition.
     * This is used for static menu definitions.
     * 
     * @return List of item stacks
     */
    @Override
    public List<ItemStack> getItemStacks() {
        // For dynamic menus, this can return empty list
        // Items are added in onCreate() method
        return List.of();
    }
}

