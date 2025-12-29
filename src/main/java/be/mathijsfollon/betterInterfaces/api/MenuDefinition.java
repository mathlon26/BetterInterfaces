package be.mathijsfollon.betterInterfaces.api;

import be.mathijsfollon.betterInterfaces.events.MenuOpenEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface MenuDefinition {
    String getId();
    int getSize();
    Component getTitle();

    List<ItemStack> getItemStacks();

    Menu create(MenuOpenContextStore ctx, CompletableFuture<MenuOpenEvent> sessionFuture);
}
