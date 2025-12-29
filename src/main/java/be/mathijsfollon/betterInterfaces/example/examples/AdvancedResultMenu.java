package be.mathijsfollon.betterInterfaces.example.examples;

import be.mathijsfollon.betterInterfaces.api.Menu;
import be.mathijsfollon.betterInterfaces.api.MenuItem;
import be.mathijsfollon.betterInterfaces.api.MenuService;
import be.mathijsfollon.betterInterfaces.api.events.MenuEventHandler;
import be.mathijsfollon.betterInterfaces.events.MenuClickEvent;
import be.mathijsfollon.betterInterfaces.events.MenuOpenEvent;
import be.mathijsfollon.betterInterfaces.menu.AbstractMenuDefinition;
import be.mathijsfollon.betterInterfaces.api.MenuOpenContextStore;
import be.mathijsfollon.betterInterfaces.menu.SimpleMenu;
import be.mathijsfollon.betterInterfaces.menu.SimpleMenuItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Result menu that shows the outcome of an action.
 * This is the final menu in the advanced example flow.
 */
public class AdvancedResultMenu extends AbstractMenuDefinition {
    
    public AdvancedResultMenu() {
        super("advanced-result-menu", "Action Result", 3);
    }

    @Override
    public Menu onCreate(Menu menu, MenuOpenContextStore ctx) {
        if (!(menu instanceof SimpleMenu simpleMenu)) {
            return menu;
        }

        // Get action details
        String actionName = ctx.get("action-name", String.class).orElse("Action");
        String actionType = ctx.get("action-type", String.class).orElse("unknown");
        boolean success = ctx.get("action-success", Boolean.class).orElse(true);

        // Result display item
        Material resultMaterial = success ? Material.EMERALD : Material.REDSTONE_BLOCK;
        ItemStack resultItem = new ItemStack(resultMaterial);
        resultItem.editMeta(meta -> {
            if (success) {
                meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize("§a§lAction Completed!"));
            } else {
                meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize("§c§lAction Failed!"));
            }
            
            meta.lore(List.of(
                Component.empty(),
                LegacyComponentSerializer.legacyAmpersand().deserialize("§7Action: §f" + actionName),
                LegacyComponentSerializer.legacyAmpersand().deserialize("§7Type: §f" + actionType),
                Component.empty()
            ));
            
            if (success) {
                meta.lore().add(LegacyComponentSerializer.legacyAmpersand().deserialize("§a✓ Successfully completed"));
            } else {
                meta.lore().add(LegacyComponentSerializer.legacyAmpersand().deserialize("§c✗ Failed to complete"));
            }
            
            meta.lore().add(Component.empty());
            meta.lore().add(LegacyComponentSerializer.legacyAmpersand().deserialize("§7Click below to return"));
        });
        simpleMenu.addItem(new SimpleMenuItem(13, resultItem)); // Center

        // Add back button if there's a previous menu
        // Pass ctx parameter so it can check for previous session even before this.context is set
        simpleMenu.addBackButton(22, ctx); // Bottom center

        // Fill with gradient
        Material gradientStart = success ? Material.GREEN_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;
        Material gradientEnd = success ? Material.LIME_STAINED_GLASS_PANE : Material.PINK_STAINED_GLASS_PANE;
        simpleMenu.fillGradient(gradientStart, gradientEnd);

        return menu;
    }

    /**
     * Handles clicks in the result menu.
     */
    @MenuEventHandler
    public void onResultClick(MenuClickEvent event) {
        // Back button is handled automatically by AbstractMenu.handleBackButton
        // Just cancel the event to prevent item movement
        event.setCancelled(true);
    }

    /**
     * Handles menu open.
     */
    @MenuEventHandler
    public void onMenuOpen(MenuOpenEvent event) {
        Player player = event.getPlayer();
        String actionName = event.getContext().get("action-name", String.class).orElse("action");
        boolean success = event.getContext().get("action-success", Boolean.class).orElse(true);
        
        if (success) {
            player.sendMessage("§aAction completed successfully!");
        } else {
            player.sendMessage("§cAction failed!");
        }
    }

    @Override
    public List<ItemStack> getItemStacks() {
        return List.of();
    }
}

