package be.mathijsfollon.betterInterfaces.example;

import be.mathijsfollon.betterInterfaces.api.Menu;
import be.mathijsfollon.betterInterfaces.api.MenuDefinition;
import be.mathijsfollon.betterInterfaces.api.MenuService;
import be.mathijsfollon.betterInterfaces.api.MenuSession;
import be.mathijsfollon.betterInterfaces.api.exceptions.MenuNotRegisteredException;
import be.mathijsfollon.betterInterfaces.example.examples.AdvancedConfirmationMenu;
import be.mathijsfollon.betterInterfaces.example.examples.AdvancedEventExample;
import be.mathijsfollon.betterInterfaces.example.examples.AdvancedResultMenu;
import be.mathijsfollon.betterInterfaces.example.examples.BasicMenuExample;
import be.mathijsfollon.betterInterfaces.example.examples.CustomMenuExample;
import be.mathijsfollon.betterInterfaces.example.examples.DynamicMenuExample;
import be.mathijsfollon.betterInterfaces.example.examples.PageableMenuExample;
import be.mathijsfollon.betterInterfaces.example.menus.ShowcaseMenu;
import be.mathijsfollon.betterInterfaces.api.MenuOpenContextStore;
import be.mathijsfollon.betterInterfaces.menu.MenuOpenContext;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Example plugin demonstrating basic usage of BetterInterfaces menu system.
 * 
 * This plugin:
 * - Registers all example menus
 * - Provides commands to open example menus
 * - Includes a showcase menu that lets you browse all examples
 * - Opens the showcase menu when players join
 * 
 * Commands:
 * - /examples - Opens the showcase menu (browse all examples)
 * - /examples <menu> - Opens a specific example menu
 * 
 * Available menus:
 * - showcase - The showcase menu (default)
 * - basic - Basic menu example
 * - pageable - Pageable menu example
 * - dynamic - Dynamic menu example
 * - custom - Custom menu example
 * - advanced - Advanced event example
 * 
 * See the examples.examples package for more comprehensive examples:
 * - BasicMenuExample: Simple menu creation and event handling
 * - PageableMenuExample: Multi-page menus with navigation
 * - DynamicMenuExample: Building menus programmatically
 * - CustomMenuExample: Extending abstract classes
 * - AdvancedEventExample: Advanced event handling patterns
 */
public class ExamplePlugin extends JavaPlugin implements Listener, CommandExecutor, TabCompleter {
    private MenuService service;
    
    // Menu ID mappings
    private static final String SHOWCASE_MENU = "showcase-menu";
    private static final String BASIC_MENU = "basic-menu";
    private static final String PAGEABLE_MENU = "pageable-menu";
    private static final String DYNAMIC_MENU = "dynamic-menu";
    private static final String CUSTOM_MENU = "custom-menu";
    private static final String ADVANCED_MENU = "advanced-event-menu";
    
    @Override
    public void onEnable() {
        // Load the MenuService from Bukkit Services API
        // This service is registered by the BetterInterfaces plugin
        service = Bukkit.getServicesManager().load(MenuService.class);

        if (service == null) {
            getLogger().warning("MenuService not found! Make sure BetterInterfaces is installed.");
            return;
        }

        // Register all menu definitions
        List<MenuDefinition> menus = Arrays.asList(
            new ShowcaseMenu(),
            new BasicMenuExample(),
            new PageableMenuExample(),
            new DynamicMenuExample(),
            new CustomMenuExample.CustomShopMenuDefinition(),
            new AdvancedEventExample(),
            new AdvancedConfirmationMenu(),
            new AdvancedResultMenu()
        );
        service.registerMenus(menus);
        
        // Register command executor
        // Note: Command must be registered in plugin.yml
        if (getCommand("examples") != null) {
            getCommand("examples").setExecutor(this);
            getCommand("examples").setTabCompleter(this);
        } else {
            getLogger().warning("Command 'examples' not found in plugin.yml! Commands may not work.");
        }
        
        // Register this class as an event listener
        Bukkit.getPluginManager().registerEvents(this, this);
        
        getLogger().info("ExamplePlugin enabled! Use /examples to open the showcase menu.");
    }

    /**
     * Handles player join events.
     * Opens the showcase menu for new players.
     * 
     * @param event The player join event
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Create a context to pass data to the menu
        MenuOpenContext ctx = new MenuOpenContext();
        ctx.put("plugin", this); // Pass plugin instance
        // Note: MenuService is automatically added to context by the service

        // Open the showcase menu after a short delay to let the player fully join
        Bukkit.getScheduler().runTaskLater(this, () -> {
            try {
                service.openMenu(player, SHOWCASE_MENU, ctx);
            } catch (MenuNotRegisteredException e) {
                getLogger().warning("Showcase menu not registered!");
            }
        }, 20L); // 1 second delay
    }

    /**
     * Handles the /examples command.
     * 
     * Usage:
     * - /examples - Opens the showcase menu
     * - /examples <menu> - Opens a specific menu
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }

        String menuId = SHOWCASE_MENU; // Default to showcase menu
        
        if (args.length > 0) {
            // Map command argument to menu ID
            switch (args[0].toLowerCase()) {
                case "showcase":
                    menuId = SHOWCASE_MENU;
                    break;
                case "basic":
                    menuId = BASIC_MENU;
                    break;
                case "pageable":
                    menuId = PAGEABLE_MENU;
                    break;
                case "dynamic":
                    menuId = DYNAMIC_MENU;
                    break;
                case "custom":
                    menuId = CUSTOM_MENU;
                    break;
                case "advanced":
                    menuId = ADVANCED_MENU;
                    break;
                default:
                    player.sendMessage("Unknown menu: " + args[0]);
                    player.sendMessage("Available menus: showcase, basic, pageable, dynamic, custom, advanced");
                    return true;
            }
        }

        // Create context
        MenuOpenContext ctx = new MenuOpenContext();
        ctx.put("plugin", this);

        // Open the menu
        try {
            service.openMenu(player, menuId, ctx);
        } catch (MenuNotRegisteredException e) {
            player.sendMessage("Error: Menu '" + menuId + "' is not registered!");
            getLogger().warning("Menu '" + menuId + "' not registered!");
        }

        return true;
    }

    /**
     * Provides tab completion for the /examples command.
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            String input = args[0].toLowerCase();
            
            List<String> menus = Arrays.asList("showcase", "basic", "pageable", "dynamic", "custom", "advanced");
            for (String menu : menus) {
                if (menu.startsWith(input)) {
                    completions.add(menu);
                }
            }
            
            return completions;
        }
        
        return new ArrayList<>();
    }

    /**
     * Handles the menu session asynchronously.
     * This demonstrates how to work with the CompletableFuture returned by getResult().
     * 
     * @param session The menu session
     */
    private void handlePlayerJoinSession(MenuSession session) {
        // The getResult() method returns a CompletableFuture that completes when the menu opens
        session.getResult().thenAccept(menuOpenEvent -> {
            // This code runs when the menu has been opened

            MenuOpenContextStore context = menuOpenEvent.getContext();

            // Retrieve data from context (typed get with Optional)
            context.get("join-message", Component.class).ifPresent(joinMessage -> {
                menuOpenEvent.getPlayer().sendMessage(joinMessage);
            });
            
            // You can also access the player, menu, and session from the event
            Player player = menuOpenEvent.getPlayer();
            Menu menu = menuOpenEvent.getMenu();
            MenuSession sessionFromEvent = menuOpenEvent.getSession();
            
            // Example: Close menu after 5 seconds
            // Bukkit.getScheduler().runTaskLater(this, () -> {
            //     sessionFromEvent.close(true);
            // }, 100L); // 100 ticks = 5 seconds
        }).exceptionally(throwable -> {
            // Handle any errors that occurred
            getLogger().warning("Error handling menu session: " + throwable.getMessage());
            return null;
        });
    }

    @Override
    public void onDisable() {
        getLogger().info("ExamplePlugin disabled!");
    }
}
