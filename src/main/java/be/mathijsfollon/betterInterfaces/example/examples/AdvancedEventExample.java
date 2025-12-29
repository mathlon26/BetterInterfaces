package be.mathijsfollon.betterInterfaces.example.examples;

import be.mathijsfollon.betterInterfaces.api.Menu;
import be.mathijsfollon.betterInterfaces.api.MenuItem;
import be.mathijsfollon.betterInterfaces.api.MenuService;
import be.mathijsfollon.betterInterfaces.api.events.MenuEventHandler;
import be.mathijsfollon.betterInterfaces.events.MenuClickEvent;
import be.mathijsfollon.betterInterfaces.events.MenuCloseEvent;
import be.mathijsfollon.betterInterfaces.events.MenuOpenEvent;
import be.mathijsfollon.betterInterfaces.menu.AbstractMenuDefinition;
import be.mathijsfollon.betterInterfaces.api.MenuOpenContextStore;
import be.mathijsfollon.betterInterfaces.menu.MenuOpenContext;
import be.mathijsfollon.betterInterfaces.menu.SimpleMenu;
import be.mathijsfollon.betterInterfaces.menu.SimpleMenuItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Advanced event handling example demonstrating:
 * - Multiple menus in a chain (selection -> confirmation -> result)
 * - Double confirmation for dangerous actions
 * - Event priority and ordering
 * - Event cancellation patterns
 * - Navigation between menus
 * - Context passing between menus
 * - Different action flows based on context
 * 
 * Flow:
 * 1. Action Selection Menu - Choose an action to perform
 * 2. Confirmation Menu - Confirm the action (simple or double confirmation)
 * 3. Result Menu - Shows the result of the action
 */
public class AdvancedEventExample extends AbstractMenuDefinition {
    
    /**
     * Creates a new AdvancedEventExample menu definition.
     */
    public AdvancedEventExample() {
        super("advanced-event-menu", "Action Selection", 4);
    }

    /**
     * Configures the action selection menu.
     */
    @Override
    public Menu onCreate(Menu menu, MenuOpenContextStore ctx) {
        if (!(menu instanceof SimpleMenu simpleMenu)) {
            return menu;
        }

        // Action selection items
        // Safe action (simple confirmation)
        ItemStack teleportItem = new ItemStack(Material.ENDER_PEARL);
        teleportItem.editMeta(meta -> {
            meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize("§bTeleport to Spawn"));
            meta.lore(List.of(
                LegacyComponentSerializer.legacyAmpersand().deserialize("§7Teleport to the world spawn"),
                LegacyComponentSerializer.legacyAmpersand().deserialize("§7Requires: Simple confirmation"),
                Component.empty(),
                LegacyComponentSerializer.legacyAmpersand().deserialize("§eClick to select")
            ));
        });
        simpleMenu.addItem(new SimpleMenuItem(10, teleportItem));

        // Purchase action (simple confirmation with cost)
        ItemStack purchaseItem = new ItemStack(Material.EMERALD);
        purchaseItem.editMeta(meta -> {
            meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize("§aPurchase Premium Item"));
            meta.lore(List.of(
                LegacyComponentSerializer.legacyAmpersand().deserialize("§7Buy a premium item for §61000 coins"),
                LegacyComponentSerializer.legacyAmpersand().deserialize("§7Requires: Simple confirmation"),
                Component.empty(),
                LegacyComponentSerializer.legacyAmpersand().deserialize("§eClick to select")
            ));
        });
        simpleMenu.addItem(new SimpleMenuItem(12, purchaseItem));

        // Dangerous action (double confirmation required)
        ItemStack deleteItem = new ItemStack(Material.TNT);
        deleteItem.editMeta(meta -> {
            meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize("§cDelete Account Data"));
            meta.lore(List.of(
                LegacyComponentSerializer.legacyAmpersand().deserialize("§7§lWARNING: This cannot be undone!"),
                LegacyComponentSerializer.legacyAmpersand().deserialize("§7Requires: §cDouble confirmation"),
                Component.empty(),
                LegacyComponentSerializer.legacyAmpersand().deserialize("§eClick to select")
            ));
        });
        simpleMenu.addItem(new SimpleMenuItem(14, deleteItem));

        // Admin action (with permission check)
        ItemStack adminItem = new ItemStack(Material.COMMAND_BLOCK);
        adminItem.editMeta(meta -> {
            meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize("§6Admin Action"));
            meta.lore(List.of(
                LegacyComponentSerializer.legacyAmpersand().deserialize("§7Execute admin command"),
                LegacyComponentSerializer.legacyAmpersand().deserialize("§7Requires: Simple confirmation"),
                Component.empty(),
                LegacyComponentSerializer.legacyAmpersand().deserialize("§eClick to select")
            ));
        });
        simpleMenu.addItem(new SimpleMenuItem(16, adminItem));

        // Fill empty slots with gradient (before adding back button)
        simpleMenu.fillGradient(Material.PURPLE_STAINED_GLASS_PANE, Material.MAGENTA_STAINED_GLASS_PANE);
        
        // Add back button if there's a previous menu (after fill so it's on top)
        // Use slot 31 (4 rows = 36 slots, slot 31 is center of bottom row 4)
        // Pass ctx parameter so it can check for previous session even before this.context is set
        simpleMenu.addBackButton(31, ctx);

        return menu;
    }

    /**
     * High priority handler - logs all clicks first.
     */
    @MenuEventHandler(priority = -10)
    public void onEarlyClick(MenuClickEvent event) {
        System.out.println("[Advanced Example] Click detected at slot " + event.getSlot() + 
            " by " + event.getPlayer().getName());
    }

    /**
     * Main click handler - processes action selection.
     */
    @MenuEventHandler(priority = 0)
    public void onActionSelection(MenuClickEvent event) {
        event.setCancelled(true);
        
        MenuItem menuItem = event.getMenuItem();
        if (menuItem == null) {
            return;
        }

        ItemStack item = menuItem.getItemStack();
        Player player = event.getPlayer();
        MenuService service = event.getContext()
            .get("menu-service", MenuService.class)
            .orElse(null);
        
        if (service == null) {
            player.sendMessage("§cError: MenuService not available!");
            return;
        }

        Material material = item.getType();
        MenuOpenContext newCtx = new MenuOpenContext();
        
        // Copy plugin reference
        event.getContext().get("plugin").ifPresent(plugin -> newCtx.put("plugin", plugin));
        
        // Determine action type and setup context
        String actionType = null;
        boolean requiresDoubleConfirm = false;
        
        if (material == Material.ENDER_PEARL) {
            actionType = "teleport";
            newCtx.put("action-name", "Teleport to Spawn");
            newCtx.put("confirmation-message", "§aAre you sure you want to teleport to spawn?");
            newCtx.put("action-cost", 0);
        } else if (material == Material.EMERALD) {
            actionType = "purchase";
            newCtx.put("action-name", "Purchase Premium Item");
            newCtx.put("confirmation-message", "§aPurchase Premium Item for §61000 coins?");
            newCtx.put("action-cost", 1000);
            requiresDoubleConfirm = false;
        } else if (material == Material.TNT) {
            actionType = "delete";
            newCtx.put("action-name", "Delete Account Data");
            newCtx.put("confirmation-message", "§c§lWARNING: This will delete all your data!");
            requiresDoubleConfirm = true; // Dangerous action requires double confirmation
        } else if (material == Material.COMMAND_BLOCK) {
            if (!player.hasPermission("betterinterfaces.admin")) {
                player.sendMessage("§cYou don't have permission for this action!");
                return;
            }
            actionType = "admin";
            newCtx.put("action-name", "Admin Command");
            newCtx.put("confirmation-message", "§6Execute admin command?");
        } else {
            return; // Unknown action
        }

        if (actionType == null) {
            return;
        }

        newCtx.put("action-type", actionType);
        newCtx.put("requires-double-confirm", requiresDoubleConfirm);
        newCtx.put("menu-service", service);

        // Open confirmation menu
        try {
            service.openMenu(player, "advanced-confirmation-menu", newCtx, event.getSession());
        } catch (Exception e) {
            player.sendMessage("§cError opening confirmation menu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Low priority handler - final logging.
     */
    @MenuEventHandler(priority = 10, ignoreCancelled = false)
    public void onLateClick(MenuClickEvent event) {
        if (event.isCancelled()) {
            System.out.println("[Advanced Example] Click was cancelled by handler");
        }
    }

    @Override
    public List<ItemStack> getItemStacks() {
        return List.of();
    }
}
