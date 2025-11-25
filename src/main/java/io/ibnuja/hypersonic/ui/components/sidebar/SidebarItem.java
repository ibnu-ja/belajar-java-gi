package io.ibnuja.hypersonic.ui.components.sidebar;

import io.ibnuja.hypersonic.navigation.Route;
import org.jetbrains.annotations.Nullable;

public record SidebarItem(String title, Route route, @Nullable String iconName) {

    public SidebarItem(String title, Route route) {
        this(title, route, null);
    }
}
