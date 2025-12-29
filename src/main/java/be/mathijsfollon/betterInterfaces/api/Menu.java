package be.mathijsfollon.betterInterfaces.api;

public interface Menu {
    void open();
    void close();
    void close(boolean silently);

    boolean isOpen();
}
