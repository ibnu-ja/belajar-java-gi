// src/main/java/io/ibnuja/hypersonic/ui/pages/PlaceholderPage.java
package io.ibnuja.hypersonic.ui.pages;

import io.ibnuja.hypersonic.navigation.Route;
import org.gnome.adw.HeaderBar;
import org.gnome.adw.NavigationPage;
import org.gnome.adw.StatusPage;
import org.gnome.adw.ToolbarView;

@SuppressWarnings("java:S110")
public class PlaceholderPage extends NavigationPage {

    public PlaceholderPage(Route route) {
        super();
        setTitle(route.title());
        setTag(route.id());

        var toolbar = new ToolbarView();
        toolbar.addTopBar(new HeaderBar());

        var status = new StatusPage();
        status.setTitle(route.title());
        status.setDescription("Under Construction");
        status.setIconName("applications-system-symbolic"); // Generic icon

        toolbar.setContent(status);
        setChild(toolbar);
    }
}
