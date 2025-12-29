package be.mathijsfollon.betterInterfaces.menu;

import be.mathijsfollon.betterInterfaces.api.*;
import be.mathijsfollon.betterInterfaces.api.events.MenuEventListener;
import be.mathijsfollon.betterInterfaces.events.MenuOpenEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Abstract base class for menu definitions.
 * Provides a foundation for creating menu definitions with event handling support.
 * 
 * Subclasses should override onCreate() to configure the menu layout.
 * The create() method can be overridden for custom menu creation logic.
 */
public abstract class AbstractMenuDefinition implements MenuDefinition, MenuEventListener {
    protected final String id;
    protected final String title;
    protected final int rows;

    /**
     * Creates a new AbstractMenuDefinition.
     * 
     * @param id The unique identifier for this menu
     * @param title The menu title
     * @param rows The number of rows (1-6)
     */
    public AbstractMenuDefinition(String id, String title, int rows) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Menu ID cannot be null or empty");
        }
        if (rows < 1 || rows > 6) {
            throw new IllegalArgumentException("Rows must be between 1 and 6");
        }
        this.id = id;
        this.title = title;
        this.rows = rows;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public int getSize() {
        return rows * 9;
    }

    @Override
    public Component getTitle() {
        return title != null ? LegacyComponentSerializer.legacyAmpersand().deserialize(title) : Component.empty();
    }

    @Override
    public List<ItemStack> getItemStacks() {
        return List.of();
    }

    /**
     * Creates a menu instance.
     * By default, creates a SimpleMenu and calls onCreate() for configuration.
     * Can be overridden for custom menu creation.
     * 
     * @param ctx The menu open context
     * @param sessionFuture Future that completes when menu opens
     * @return The created menu
     */
    @Override
    public Menu create(MenuOpenContextStore ctx, CompletableFuture<MenuOpenEvent> sessionFuture) {
        Plugin plugin = ctx.get("plugin", Plugin.class).orElse(null);
        if (plugin == null) {
            throw new IllegalStateException("Plugin must be set in MenuOpenContextStore");
        }

        Player player = ctx.getPlayer().orElse(null);
        if (player == null) {
            throw new IllegalStateException("Player must be set in MenuOpenContextStore");
        }

        MenuService service = ctx.get("menu-service", MenuService.class).orElse(null);
        if (service == null) {
            throw new IllegalStateException("MenuService must be set in MenuOpenContextStore");
        }

        // Create a simple menu by default
        SimpleMenu menu = new SimpleMenu(
            getTitle(),
            getSize(),
            player,
            service.getEventManager(),
            plugin
        );

        // Call onCreate for configuration
        return onCreate(menu, ctx);
    }

    /**
     * Called after menu creation to configure the menu layout.
     * Subclasses should override this method to add items and configure the menu.
     * 
     * @param menu The menu instance to configure
     * @param ctx The menu open context
     * @return The configured menu (usually the same instance)
     */
    public abstract Menu onCreate(Menu menu, MenuOpenContextStore ctx);
}
