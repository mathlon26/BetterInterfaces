package be.mathijsfollon.betterInterfaces.example.examples;

import be.mathijsfollon.betterInterfaces.api.Menu;
import be.mathijsfollon.betterInterfaces.api.events.MenuEventHandler;
import be.mathijsfollon.betterInterfaces.events.MenuClickEvent;
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
 * Dynamic menu example demonstrating:
 * - Building menus programmatically at runtime
 * - Adding/removing items dynamically
 * - Updating menu content based on player data
 * - Conditional item placement
 * 
 * This example creates a dynamic player stats menu that shows different
 * items based on the player's state.
 */
public class DynamicMenuExample extends AbstractMenuDefinition {
    
    /**
     * Creates a new DynamicMenuExample menu definition.
     */
    public DynamicMenuExample() {
        super("dynamic-menu", "Your Stats", 3);
    }

    /**
     * Configures the menu after creation.
     * This is called automatically by the default create() method.
     * The menu is built dynamically based on player state.
     * 
     * @param menu The menu instance to configure
     * @param ctx The menu open context
     * @return The configured menu
     */
    @Override
    public Menu onCreate(Menu menu, MenuOpenContextStore ctx) {
        // Cast to SimpleMenu to add items
        if (!(menu instanceof SimpleMenu simpleMenu)) {
            return menu;
        }

        Player player = ctx.getPlayer().orElse(null);
        if (player == null) {
            return menu;
        }

        // Build menu dynamically based on player data
        buildDynamicMenu(simpleMenu, player, ctx);

        // Fill empty slots with gradient for visual appeal (before adding back button)
        simpleMenu.fillGradient(Material.GREEN_STAINED_GLASS_PANE, Material.LIME_STAINED_GLASS_PANE);
        
        // Add back button if there's a previous menu (after fill so it's on top)
        // Use slot 22 (bottom center) to avoid conflicts with action items
        // Pass ctx parameter so it can check for previous session even before this.context is set
        simpleMenu.addBackButton(22, ctx);

        return menu;
    }

    /**
     * Builds the menu content dynamically based on player state.
     * 
     * @param menu The menu to build
     * @param player The player viewing the menu
     * @param ctx The menu context
     */
    private void buildDynamicMenu(SimpleMenu menu, Player player, MenuOpenContextStore ctx) {
        // Row 1: Player Info
        addPlayerInfoRow(menu, player, 0);

        // Row 2: Stats (dynamically based on player data)
        addStatsRow(menu, player, 9);

        // Row 3: Actions (conditional buttons)
        addActionsRow(menu, player, 18);
    }

    /**
     * Adds player information to the first row.
     * 
     * @param menu The menu to add items to
     * @param player The player
     * @param startSlot Starting slot for this row
     */
    private void addPlayerInfoRow(SimpleMenu menu, Player player, int startSlot) {
        // Player head (if available)
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        playerHead.editMeta(meta -> {
            meta.displayName(Component.text(player.getName()));
            meta.lore(List.of(
                Component.text("Level: " + player.getLevel()),
                Component.text("Health: " + Math.round(player.getHealth()) + "/" + Math.round(player.getMaxHealth()))
            ));
        });
        menu.addItem(new SimpleMenuItem(startSlot + 4, playerHead)); // Center of row
    }

    /**
     * Adds statistics row with dynamic content.
     * 
     * @param menu The menu to add items to
     * @param player The player
     * @param startSlot Starting slot for this row
     */
    private void addStatsRow(SimpleMenu menu, Player player, int startSlot) {
        // Experience
        ItemStack expItem = new ItemStack(Material.EXPERIENCE_BOTTLE);
        expItem.editMeta(meta -> {
            meta.displayName(Component.text("Experience"));
            meta.lore(List.of(
                Component.text("Level: " + player.getLevel()),
                Component.text("Exp: " + player.getExp() * 100 + "%")
            ));
        });
        menu.addItem(new SimpleMenuItem(startSlot + 1, expItem));

        // Food level
        ItemStack foodItem = new ItemStack(Material.COOKED_BEEF);
        foodItem.editMeta(meta -> {
            meta.displayName(Component.text("Hunger"));
            meta.lore(List.of(
                Component.text("Food: " + player.getFoodLevel() + "/20"),
                Component.text("Saturation: " + Math.round(player.getSaturation()))
            ));
        });
        menu.addItem(new SimpleMenuItem(startSlot + 4, foodItem));

        // Game mode
        ItemStack gameModeItem = new ItemStack(
            player.getGameMode() == org.bukkit.GameMode.CREATIVE 
                ? Material.DIAMOND 
                : Material.IRON_SWORD
        );
        gameModeItem.editMeta(meta -> {
            meta.displayName(Component.text("Game Mode"));
            meta.lore(List.of(
                Component.text(player.getGameMode().toString())
            ));
        });
        menu.addItem(new SimpleMenuItem(startSlot + 7, gameModeItem));
    }

    /**
     * Adds action buttons based on player permissions/state.
     * 
     * @param menu The menu to add items to
     * @param player The player
     * @param startSlot Starting slot for this row
     */
    private void addActionsRow(SimpleMenu menu, Player player, int startSlot) {
        // Teleport home button (if player has home)
        if (hasHome(player)) {
            ItemStack homeItem = new ItemStack(Material.COMPASS);
            homeItem.editMeta(meta -> {
                meta.displayName(Component.text("Teleport Home"));
                meta.lore(List.of(Component.text("Click to go home")));
            });
            menu.addItem(new SimpleMenuItem(startSlot + 2, homeItem));
        }

        // Settings button (always available)
        ItemStack settingsItem = new ItemStack(Material.REDSTONE);
        settingsItem.editMeta(meta -> {
            meta.displayName(Component.text("Settings"));
            meta.lore(List.of(Component.text("Open settings menu")));
        });
        menu.addItem(new SimpleMenuItem(startSlot + 4, settingsItem));

        // Admin panel (only if player has permission)
        if (player.hasPermission("betterinterfaces.admin")) {
            ItemStack adminItem = new ItemStack(Material.COMMAND_BLOCK);
            adminItem.editMeta(meta -> {
                meta.displayName(Component.text("Admin Panel"));
                meta.lore(List.of(Component.text("Administrative tools")));
            });
            menu.addItem(new SimpleMenuItem(startSlot + 6, adminItem));
        }
    }

    /**
     * Example method to check if player has a home.
     * In a real implementation, this would check your home plugin or system.
     * 
     * @param player The player to check
     * @return true if player has a home
     */
    private boolean hasHome(Player player) {
        // Example: always return true for demo
        // In real implementation, check your home system
        return true;
    }

    /**
     * Handles clicks on dynamic menu items.
     * 
     * @param event The click event
     */
    @MenuEventHandler
    public void onDynamicMenuClick(MenuClickEvent event) {
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        Player player = event.getPlayer();
        Material material = item.getType();

        switch (material) {
            case COMPASS:
                // Teleport home logic
                player.sendMessage("Teleporting to home...");
                // player.teleport(getHomeLocation(player));
                event.setCancelled(true);
                break;

            case REDSTONE:
                // Open settings
                player.sendMessage("Opening settings...");
                // OpenSettingsMenu(player);
                event.setCancelled(true);
                break;

            case COMMAND_BLOCK:
                // Admin panel
                if (player.hasPermission("betterinterfaces.admin")) {
                    player.sendMessage("Opening admin panel...");
                    // OpenAdminMenu(player);
                }
                event.setCancelled(true);
                break;

            default:
                // Prevent item removal for all items
                event.setCancelled(true);
                break;
        }
    }

    /**
     * Called when menu is opened.
     * Can be used to refresh dynamic content.
     * 
     * @param event The open event
     */
    @MenuEventHandler
    public void onDynamicMenuOpen(MenuOpenEvent event) {
        Player player = event.getPlayer();
        player.sendMessage("Your stats menu is ready!");
        
        // Menu could be refreshed here if needed
        if (event.getMenu() instanceof SimpleMenu simpleMenu) {
            // Example: Update menu items based on latest player data
            // refreshMenuContent(simpleMenu, player);
        }
    }

    @Override
    public List<ItemStack> getItemStacks() {
        return List.of();
    }
}

