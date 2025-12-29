package be.mathijsfollon.betterInterfaces.api.events;

public interface MenuEventManager {
    void registerListener(MenuEventListener listener);

    void unregisterListener(MenuEventListener listener);

    void fireEvent(MenuEvent event);

    void clear();
}
