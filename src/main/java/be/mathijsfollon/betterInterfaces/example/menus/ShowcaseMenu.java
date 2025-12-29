package be.mathijsfollon.betterInterfaces.example.menus;

import be.mathijsfollon.betterInterfaces.api.Menu;
import be.mathijsfollon.betterInterfaces.api.MenuService;
import be.mathijsfollon.betterInterfaces.api.events.MenuEventHandler;
import be.mathijsfollon.betterInterfaces.events.MenuClickEvent;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Showcase menu that demonstrates the menu system by allowing players
 * to open all the example menus.
 * 
 * This menu serves as both an example and a navigation hub for exploring
 * different menu features.
 */
public
class ShowcaseMenu extends AbstractMenuDefinition {
    
    private static final Map<Integer, String> MENU_ITEMS = new HashMap<>();
    
    static {
        MENU_ITEMS.put(0, "basic-menu");
        MENU_ITEMS.put(2, "pageable-menu");
        MENU_ITEMS.put(4, "dynamic-menu");
        MENU_ITEMS.put(6, "custom-menu");
        MENU_ITEMS.put(8, "advanced-event-menu");
    }

    public ShowcaseMenu() {
        super("showcase-menu", "&6Menu Showcase", 3); // 3 rows = 27 slots
    }

    @Override
    public Menu onCreate(Menu menu, MenuOpenContextStore ctx) {
        if (!(menu instanceof SimpleMenu simpleMenu)) {
            return menu;
        }

        // Put menu buttons on top row (slots 0-8)
        // Basic Menu Example
        ItemStack basicItem = new ItemStack(Material.BOOK);
        basicItem.editMeta(meta -> {
            meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize("§6Basic Menu"));
            meta.lore(List.of(
                LegacyComponentSerializer.legacyAmpersand().deserialize("§7Simple menu with items"),
                LegacyComponentSerializer.legacyAmpersand().deserialize("§7and click handlers"),
                Component.empty(),
                LegacyComponentSerializer.legacyAmpersand().deserialize("§eClick to open!")
            ));
        });
        simpleMenu.addItem(new SimpleMenuItem(0, basicItem));

        // Pageable Menu Example
        ItemStack pageableItem = new ItemStack(Material.BOOKSHELF);
        pageableItem.editMeta(meta -> {
            meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize("§6Pageable Menu"));
            meta.lore(List.of(
                LegacyComponentSerializer.legacyAmpersand().deserialize("§7Multi-page menu with"),
                LegacyComponentSerializer.legacyAmpersand().deserialize("§7navigation controls"),
                Component.empty(),
                LegacyComponentSerializer.legacyAmpersand().deserialize("§eClick to open!")
            ));
        });
        simpleMenu.addItem(new SimpleMenuItem(2, pageableItem));

        // Dynamic Menu Example
        ItemStack dynamicItem = new ItemStack(Material.COMPASS);
        dynamicItem.editMeta(meta -> {
            meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize("§6Dynamic Menu"));
            meta.lore(List.of(
                LegacyComponentSerializer.legacyAmpersand().deserialize("§7Menu that adapts based"),
                LegacyComponentSerializer.legacyAmpersand().deserialize("§7on player data"),
                Component.empty(),
                LegacyComponentSerializer.legacyAmpersand().deserialize("§eClick to open!")
            ));
        });
        simpleMenu.addItem(new SimpleMenuItem(4, dynamicItem));

        // Custom Menu Example
        ItemStack customItem = new ItemStack(Material.ANVIL);
        customItem.editMeta(meta -> {
            meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize("§6Custom Menu"));
            meta.lore(List.of(
                LegacyComponentSerializer.legacyAmpersand().deserialize("§7Extended AbstractMenu"),
                LegacyComponentSerializer.legacyAmpersand().deserialize("§7for custom behavior"),
                Component.empty(),
                LegacyComponentSerializer.legacyAmpersand().deserialize("§eClick to open!")
            ));
        });
        simpleMenu.addItem(new SimpleMenuItem(6, customItem));

        // Advanced Event Example
        ItemStack advancedItem = new ItemStack(Material.REDSTONE);
        advancedItem.editMeta(meta -> {
            meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize("§6Advanced Events"));
            meta.lore(List.of(
                LegacyComponentSerializer.legacyAmpersand().deserialize("§7Event priorities and"),
                LegacyComponentSerializer.legacyAmpersand().deserialize("§7cancellation handling"),
                Component.empty(),
                LegacyComponentSerializer.legacyAmpersand().deserialize("§eClick to open!")
            ));
        });
        simpleMenu.addItem(new SimpleMenuItem(8, advancedItem));

        // Fill empty slots with gradient
        simpleMenu.fillGradient(Material.CYAN_STAINED_GLASS, Material.BLUE_STAINED_GLASS_PANE);

        // Try to add back button (will return null if no previous session, which is fine for root menu)
        simpleMenu.addBackButton(18);
        
        // Add close button in bottom right
        simpleMenu.addCloseButton(26);

        return menu;
    }

    /**
     * Handles clicks in the showcase menu.
     * Opens the selected example menu when clicked.
     */
    @MenuEventHandler
    public void onClick(MenuClickEvent event) {
        event.setCancelled(true);
        
        int slot = event.getSlot();
        String menuId = MENU_ITEMS.get(slot);
        
        if (menuId == null) {
            return;
        }

        Player player = event.getPlayer();
        MenuService service = event.getContext()
            .get("menu-service", MenuService.class)
            .orElse(null);
        
        if (service == null) {
            player.sendMessage("Error: MenuService not available!");
            return;
        }

        // Create context for the new menu
        MenuOpenContext ctx = new MenuOpenContext();
        ctx.put("plugin", event.getContext().get("plugin").orElse(null));
        
        // Add specific context data for different menus
        if (menuId.equals("advanced-event-menu")) {
            // Set different action types based on which item was clicked in showcase
            // This is a demo - in real use, you'd set this when opening the menu directly
            ctx.put("action-type", "demo-action");
            ctx.put("confirmation-message", "Are you sure you want to execute this demo action?");
        } else if (menuId.equals("pageable-menu")) {
            ctx.put("shop-type", "general");
        } else if (menuId.equals("custom-menu")) {
            ctx.put("shop-type", "custom");
        }
        
        // Open the selected menu with navigation support (current menu becomes previous)
        try {
            service.openMenu(player, menuId, ctx, event.getSession());
        } catch (Exception e) {
            player.sendMessage("§cError opening menu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<ItemStack> getItemStacks() {
        return List.of();
    }
}

