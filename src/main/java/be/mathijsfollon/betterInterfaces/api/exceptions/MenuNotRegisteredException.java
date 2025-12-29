package be.mathijsfollon.betterInterfaces.api.exceptions;

public class MenuNotRegisteredException extends RuntimeException {
    public MenuNotRegisteredException(String menuId) {
        super("Could not find a menu registered under id: " + menuId);
    }
}
