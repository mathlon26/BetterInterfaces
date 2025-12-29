package be.mathijsfollon.betterInterfaces;

import be.mathijsfollon.betterInterfaces.api.MenuService;
import be.mathijsfollon.betterInterfaces.service.BetterInterfacesMenuService;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public final class BetterInterfaces extends JavaPlugin {
    private MenuService service;

    @Override
    public void onEnable() {
        service = new BetterInterfacesMenuService();
        Bukkit.getServicesManager().register(MenuService.class, service, this, ServicePriority.High);
        
        // Initialize examples (temporary integration)
        Examples examples = new Examples(this, service);
        examples.initialize();
    }

    @Override
    public void onDisable() {
        Bukkit.getServicesManager().unregister(this);
    }
}
