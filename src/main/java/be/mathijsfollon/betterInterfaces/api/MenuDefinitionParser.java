package be.mathijsfollon.betterInterfaces.api;

import java.util.List;

public interface MenuDefinitionParser {
    MenuDefinition loadMenuDefinition(String fileResourcePath);
    List<MenuDefinition> loadMenuDefinitions(String folderResourcePath, boolean recursive);
}
