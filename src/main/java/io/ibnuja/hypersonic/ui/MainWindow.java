package io.ibnuja.hypersonic.ui;

import io.ibnuja.hypersonic.Hypersonic;
import io.ibnuja.hypersonic.navigation.Route.Routes;
import io.ibnuja.hypersonic.state.App;
import io.ibnuja.hypersonic.ui.components.sidebar.SidebarItem;
import io.ibnuja.hypersonic.ui.components.sidebar.SidebarRow;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.gnome.adw.ApplicationWindow;
import org.gnome.adw.NavigationPage;
import org.gnome.adw.NavigationSplitView;
import org.gnome.adw.NavigationView;
import org.gnome.gio.Settings;
import org.gnome.gtk.ListBox;
import org.gnome.gtk.ListBoxRow;
import org.javagi.gobject.annotations.InstanceInit;
import org.javagi.gtk.annotations.GtkChild;
import org.javagi.gtk.annotations.GtkTemplate;

import java.util.List;

@GtkTemplate(ui = "/io/ibnuja/Hypersonic/window.ui", name = "MainWindow")
@SuppressWarnings({"java:S110", "java:S112", "java:S125"})
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class MainWindow extends ApplicationWindow {

    @GtkChild(name = "navigation_view")
    public NavigationView navigationView;

    @GtkChild(name = "split_view")
    public NavigationSplitView splitView;

    @GtkChild(name = "home_listbox")
    public ListBox homeListBox;

    protected Settings settings;

    public MainWindow(Hypersonic.Application app) {
        setApplication(app);
        Hypersonic.appModel.addListener(this::handleEvent);
        navigationView.replace(List.of(Routes.HOME.page()).toArray(NavigationPage[]::new));
        setupSidebar();
    }

    //register.map Event
    private void handleEvent(App.Event event) {
        if (splitView.getCollapsed()) {
            splitView.setShowContent(true);
        }
        switch (event) {
            case App.Event.NavigationPushed(var route) -> {
                navigationView.push(route.page());
                logStackLevel("After Push");
            }
            case App.Event.NavigationPopped _ -> {
                navigationView.pop();
                logStackLevel("After Pop");
            }
            case App.Event.NavigationReset(var route) -> {
                navigationView.replace(List.of(route.page()).toArray(NavigationPage[]::new));
                logStackLevel("Reset");
            }
        }
    }

    private void logStackLevel(String context) {
        int depth = 0;
        NavigationPage current = navigationView.getVisiblePage();
        String visibleTitle = (current != null) ? current.getTitle() : "null";

        while (current != null) {
            depth++;
            current = navigationView.getPreviousPage(current);
        }

        log.debug("[Navigation] {}: Depth={}, Visible='{}'", context, depth, visibleTitle);
    }

    @InstanceInit
    @SuppressWarnings("unused")
    public void init() {
        settings = new Settings("io.ibnuja.Hypersonic");
    }

    @SuppressWarnings("java:S1602")
    private void setupSidebar() {
        List<SidebarItem> items = List.of(
                new SidebarItem("Home", Routes.HOME, "user-home-symbolic"),
                new SidebarItem("Now Playing", Routes.NOW_PLAYING),
                new SidebarItem("Albums", Routes.ALBUM, "library-album-symbolic"),
                new SidebarItem("Artists", Routes.ARTISTS),
                new SidebarItem("Songs", Routes.SONGS, "library-music-symbolic")
        );

        for (SidebarItem item : items) {
            var row = new SidebarRow();
            row.setItem(item);
            homeListBox.append(row);
        }

        homeListBox.onRowActivated((ListBoxRow row) -> {
            if (row instanceof SidebarRow sidebarRow) {
                String title = sidebarRow.title.getLabel();
                items.stream()
                        .filter(i -> i.title().equals(title))
                        .findFirst()
                        .ifPresent(i -> {
                            navigationView.replace(List.of(i.route().page()).toArray(NavigationPage[]::new));
                        });
            }
        });
    }
}
