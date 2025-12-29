# BetterInterfaces Examples

This package contains comprehensive examples demonstrating various features of the BetterInterfaces menu system.

## Example Structure

- **BasicMenuExample** - Simple menu creation and basic event handling
- **PageableMenuExample** - Multi-page menus with navigation
- **DynamicMenuExample** - Building menus programmatically
- **CustomMenuExample** - Extending abstract classes for custom behavior
- **AdvancedEventExample** - Advanced event handling patterns

## Getting Started

All examples assume you have a MenuService available via the Bukkit Services API:

```java
MenuService service = Bukkit.getServicesManager().load(MenuService.class);
```

Register your menu definitions and open menus using the service.

