package be.mathijsfollon.betterInterfaces;

import be.mathijsfollon.betterInterfaces.api.MenuDefinition;
import be.mathijsfollon.betterInterfaces.api.MenuOpenContextStore;
import be.mathijsfollon.betterInterfaces.api.MenuService;
import be.mathijsfollon.betterInterfaces.api.exceptions.MenuNotRegisteredException;
import be.mathijsfollon.betterInterfaces.example.examples.AdvancedConfirmationMenu;
import be.mathijsfollon.betterInterfaces.example.examples.AdvancedEventExample;
import be.mathijsfollon.betterInterfaces.example.examples.AdvancedResultMenu;
import be.mathijsfollon.betterInterfaces.example.examples.BasicMenuExample;
import be.mathijsfollon.betterInterfaces.example.examples.CustomMenuExample;
import be.mathijsfollon.betterInterfaces.example.examples.DynamicMenuExample;
import be.mathijsfollon.betterInterfaces.example.examples.PageableMenuExample;
import be.mathijsfollon.betterInterfaces.example.menus.ShowcaseMenu;
import be.mathijsfollon.betterInterfaces.menu.MenuOpenContext;
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
 * Temporary Examples class for demonstrating BetterInterfaces menu system.
 * This class registers example menus and provides commands to access them.
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
 */
public class Examples implements Listener, CommandExecutor, TabCompleter {
    private final JavaPlugin plugin;
    private final MenuService service;
    
    // Menu ID mappings
    private static final String SHOWCASE_MENU = "showcase-menu";
    private static final String BASIC_MENU = "basic-menu";
    private static final String PAGEABLE_MENU = "pageable-menu";
    private static final String DYNAMIC_MENU = "dynamic-menu";
    private static final String CUSTOM_MENU = "custom-menu";
    private static final String ADVANCED_MENU = "advanced-event-menu";
    
    public Examples(JavaPlugin plugin, MenuService service) {
        this.plugin = plugin;
        this.service = service;
    }
    
    /**
     * Initializes the examples by registering menus and commands.
     */
    public void initialize() {
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
        org.bukkit.command.PluginCommand command = plugin.getCommand("examples");
        if (command != null) {
            command.setExecutor(this);
            command.setTabCompleter(this);
        } else {
            plugin.getLogger().warning("Command 'examples' not found in plugin.yml! Commands may not work.");
        }
        
        // Register this class as an event listener
        Bukkit.getPluginManager().registerEvents(this, plugin);
        
        plugin.getLogger().info("Examples initialized! Use /examples to open the showcase menu.");
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
        MenuOpenContextStore ctx = new MenuOpenContext();
        ctx.put("plugin", plugin);

        // Open the showcase menu after a short delay to let the player fully join
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            try {
                service.openMenu(player, SHOWCASE_MENU, ctx);
            } catch (MenuNotRegisteredException e) {
                plugin.getLogger().warning("Showcase menu not registered!");
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
        MenuOpenContextStore ctx = new MenuOpenContext();
        ctx.put("plugin", plugin);

        // Open the menu
        try {
            service.openMenu(player, menuId, ctx);
        } catch (MenuNotRegisteredException e) {
            player.sendMessage("Error: Menu '" + menuId + "' is not registered!");
            plugin.getLogger().warning("Menu '" + menuId + "' not registered!");
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
}

