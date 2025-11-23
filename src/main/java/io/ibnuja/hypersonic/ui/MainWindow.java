package io.ibnuja.hypersonic.ui;

import io.ibnuja.hypersonic.Hypersonic;
import io.ibnuja.hypersonic.navigation.Route;
import io.ibnuja.hypersonic.navigation.ScreenFactory;
import io.ibnuja.hypersonic.ui.components.sidebar.SidebarItem;
import io.ibnuja.hypersonic.ui.components.sidebar.SidebarRow;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.gnome.adw.ApplicationWindow;
import org.gnome.adw.NavigationSplitView;
import org.gnome.adw.Toast;
import org.gnome.gio.Settings;
import org.gnome.gtk.ListBox;
import org.gnome.gtk.ListBoxRow;
import org.gnome.gtk.Stack;
import org.gnome.gtk.Widget;
import org.javagi.gobject.annotations.InstanceInit;
import org.javagi.gtk.annotations.GtkChild;
import org.javagi.gtk.annotations.GtkTemplate;

import java.util.List;

@GtkTemplate(ui = "/io/ibnuja/hypersonic/window.ui", name = "MainWindow")
@SuppressWarnings({"java:S110", "java:S112", "java:S125"})
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class MainWindow extends ApplicationWindow {

    //@GtkChild(name = "toast_overlay")
    //public ToastOverlay toastOverlay;

    @GtkChild(name = "navigation_stack")
    public Stack navigationStack;

    @GtkChild(name = "split_view")
    public NavigationSplitView splitView;

    @GtkChild(name = "home_listbox")
    public ListBox homeListBox;

    private final ScreenFactory screenFactory;

    protected Settings settings;

    public MainWindow(Hypersonic.Application app) {
        setApplication(app);

        this.screenFactory = new ScreenFactory();
        navigate(new Route.Home());
        log.info("homepage loaded");
        setupSidebar();
    }

    @InstanceInit
    @SuppressWarnings("unused")
    public void init() {
        settings = new Settings("io.ibnuja.Hypersonic");
    }

    public void navigate(Route route) {
        String id = route.name();
        if (navigationStack.getChildByName(id) == null) {
            Widget view = screenFactory.create(route);
            if (view != null) {
                navigationStack.addNamed(view, id);
            } else {
                log.warn("No view created for route: {}", id);
                return;
            }
        }
        navigationStack.setVisibleChildName(id);
        if (splitView.getCollapsed()) {
            splitView.setShowContent(true);
        }
    }

    @SuppressWarnings("unused")
    private void showToast(String message) {
        Toast toast = new Toast(message);
        //toastOverlay.addToast(toast);
    }

    private void setupSidebar() {
        List<SidebarItem> items = List.of(
                new SidebarItem("Home", new Route.Home(), "user-home-symbolic"),
                new SidebarItem("Now Playing", new Route.NowPlaying()),
                new SidebarItem("Albums", new Route.Album()),
                new SidebarItem("Artists", new Route.Artists()),
                new SidebarItem("Songs", new Route.Songs())
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
                        .ifPresent(i -> navigate(i.route()));
            }
        });
    }
}
