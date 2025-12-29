package be.mathijsfollon.betterInterfaces.example.examples;

import be.mathijsfollon.betterInterfaces.api.Menu;
import be.mathijsfollon.betterInterfaces.api.MenuItem;
import be.mathijsfollon.betterInterfaces.api.MenuService;
import be.mathijsfollon.betterInterfaces.api.MenuSession;
import be.mathijsfollon.betterInterfaces.api.events.MenuEventHandler;
import be.mathijsfollon.betterInterfaces.events.MenuClickEvent;
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
import java.util.Optional;

/**
 * Confirmation menu for advanced event example.
 * Supports both simple and double confirmation flows.
 */
public class AdvancedConfirmationMenu extends AbstractMenuDefinition {
    
    public AdvancedConfirmationMenu() {
        super("advanced-confirmation-menu", "Confirm Action", 3);
    }

    @Override
    public Menu onCreate(Menu menu, MenuOpenContextStore ctx) {
        if (!(menu instanceof SimpleMenu simpleMenu)) {
            return menu;
        }

        // Get action details from context
        String actionName = ctx.get("action-name", String.class)
            .orElse("this action");
        String confirmationMessage = ctx.get("confirmation-message", String.class)
            .orElse("Are you sure?");
        boolean requiresDoubleConfirm = ctx.get("requires-double-confirm", Boolean.class)
            .orElse(false);
        
        // Track confirmation level
        boolean isSecondConfirm = ctx.get("is-second-confirm", Boolean.class).orElse(false);

        // Display confirmation message in center
        ItemStack infoItem = new ItemStack(Material.BOOK);
        infoItem.editMeta(meta -> {
            meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize("§e" + actionName));
            meta.lore(List.of(
                Component.empty(),
                Component.text(confirmationMessage),
                Component.empty()
            ));
            
            if (requiresDoubleConfirm && !isSecondConfirm) {
                meta.lore().add(LegacyComponentSerializer.legacyAmpersand().deserialize("§c§lThis requires double confirmation!"));
                meta.lore().add(LegacyComponentSerializer.legacyAmpersand().deserialize("§7You will be asked to confirm again."));
            } else if (isSecondConfirm) {
                meta.lore().add(LegacyComponentSerializer.legacyAmpersand().deserialize("§c§lFinal Confirmation"));
                meta.lore().add(LegacyComponentSerializer.legacyAmpersand().deserialize("§7This is your last chance to cancel!"));
            }
            
            meta.lore().add(Component.empty());
            meta.lore().add(LegacyComponentSerializer.legacyAmpersand().deserialize("§7Click §aConfirm §7or §cCancel §7below"));
        });
        simpleMenu.addItem(new SimpleMenuItem(13, infoItem, false)); // Center (not movable)

        // Confirm button (green wool)
        ItemStack confirmButton = new ItemStack(Material.GREEN_WOOL);
        confirmButton.editMeta(meta -> {
            if (isSecondConfirm) {
                meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize("§a§lFINAL CONFIRM"));
            } else {
                meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize("§aConfirm"));
            }
            meta.lore(List.of(LegacyComponentSerializer.legacyAmpersand().deserialize("§7Click to confirm this action")));
        });
        simpleMenu.addItem(new SimpleMenuItem(11, confirmButton, false)); // Left (not movable)

        // Cancel button (red wool)
        ItemStack cancelButton = new ItemStack(Material.RED_WOOL);
        cancelButton.editMeta(meta -> {
            meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize("§cCancel"));
            meta.lore(List.of(LegacyComponentSerializer.legacyAmpersand().deserialize("§7Click to cancel")));
        });
        simpleMenu.addItem(new SimpleMenuItem(15, cancelButton, false)); // Right (not movable)

        // Add back button
        // Pass ctx parameter so it can check for previous session even before this.context is set
        simpleMenu.addBackButton(22, ctx); // Bottom center

        // Fill with gradient
        simpleMenu.fillGradient(Material.RED_STAINED_GLASS_PANE, Material.ORANGE_STAINED_GLASS_PANE);

        return menu;
    }

    /**
     * Early handler for logging.
     */
    @MenuEventHandler(priority = -10)
    public void onEarlyClick(MenuClickEvent event) {
        System.out.println("[Confirmation Menu] Click at slot " + event.getSlot());
    }

    /**
     * Main confirmation handler.
     * Cancels ALL clicks in the confirmation menu to prevent item movement.
     */
    @MenuEventHandler(priority = 0)
    public void onConfirmationClick(MenuClickEvent event) {
        // Always cancel clicks to prevent any item movement
        event.setCancelled(true);
        
        MenuItem menuItem = event.getMenuItem();
        if (menuItem == null) {
            // Even if there's no menu item, cancel the event to prevent moving glass panes or empty slots
            return;
        }

        ItemStack item = menuItem.getItemStack();
        Player player = event.getPlayer();
        MenuOpenContextStore ctx = event.getContext();
        MenuService service = ctx.get("menu-service", MenuService.class).orElse(null);
        
        if (service == null) {
            player.sendMessage("§cError: MenuService not available!");
            return;
        }

        Material material = item.getType();
        boolean requiresDoubleConfirm = ctx.get("requires-double-confirm", Boolean.class).orElse(false);
        boolean isSecondConfirm = ctx.get("is-second-confirm", Boolean.class).orElse(false);

        // Handle confirmation
        if (material == Material.GREEN_WOOL) {
            if (requiresDoubleConfirm && !isSecondConfirm) {
                // First confirmation - ask again
                player.sendMessage("§eFirst confirmation received. Asking for final confirmation...");
                
                // Create new context for second confirmation
                MenuOpenContext secondCtx = new MenuOpenContext();
                ctx.get("plugin").ifPresent(plugin -> secondCtx.put("plugin", plugin));
                ctx.get("action-type").ifPresent(type -> secondCtx.put("action-type", type));
                ctx.get("action-name").ifPresent(name -> secondCtx.put("action-name", name));
                ctx.get("confirmation-message").ifPresent(msg -> secondCtx.put("confirmation-message", msg));
                ctx.get("action-cost").ifPresent(cost -> secondCtx.put("action-cost", cost));
                secondCtx.put("requires-double-confirm", true);
                secondCtx.put("is-second-confirm", true); // Mark as second confirmation
                secondCtx.put("menu-service", service);
                
                // Open second confirmation menu
                // Close current menu first (silently) to allow the second confirmation to open
                // Pass current session as previous session so back button works
                try {
                    event.getSession().close(true);
                    service.openMenu(player, "advanced-confirmation-menu", secondCtx, event.getSession());
                } catch (Exception e) {
                    player.sendMessage("§cError: " + e.getMessage());
                }
            } else {
                // Confirmed! Execute action and close menu
                player.sendMessage("§aAction confirmed!");
                
                // Execute the action
                String actionType = ctx.get("action-type", String.class).orElse("unknown");
                executeAction(player, ctx, actionType);
                
                // Close the confirmation menu
                event.getSession().close(true);
            }
        }
        // Handle cancellation - navigate back to the original action menu
        else if (material == Material.RED_WOOL) {
            player.sendMessage("§cAction cancelled.");
            
            // Navigate back to the original action selection menu using goBack()
            MenuSession currentSession = event.getSession();
            if (!currentSession.goBack()) {
                // Fallback: just close the menu if no previous session
                currentSession.close(true);
            }
        }
    }

    /**
     * Handles menu open - makes it uncloseable until decision is made.
     */
    @MenuEventHandler
    public void onMenuOpen(MenuOpenEvent event) {
        if (event.getMenu() instanceof SimpleMenu menu) {
            menu.setUncloseable(true);
        }
        
        Player player = event.getPlayer();
        String actionName = event.getContext().get("action-name", String.class).orElse("action");
        player.sendMessage("§ePlease confirm: " + actionName);
    }

    /**
     * Executes the confirmed action.
     */
    private void executeAction(Player player, MenuOpenContextStore ctx, String actionType) {
        switch (actionType) {
            case "teleport":
                player.sendMessage("§bTeleporting to spawn...");
                // player.teleport(spawnLocation);
                break;
                
            case "purchase":
                Integer cost = ctx.get("action-cost", Integer.class).orElse(0);
                player.sendMessage("§aPurchase successful! Cost: §6" + cost + " coins");
                // Deduct coins, give item, etc.
                break;
                
            case "delete":
                player.sendMessage("§cAccount data deleted!");
                // Delete player data
                break;
                
            case "admin":
                player.sendMessage("§6Admin command executed!");
                // Execute admin command
                break;
                
            default:
                player.sendMessage("§eAction executed: " + actionType);
                break;
        }
    }

    @Override
    public List<ItemStack> getItemStacks() {
        return List.of();
    }
}

