package io.ibnuja.hypersonic.ui;

import io.ibnuja.hypersonic.Hypersonic;
import io.ibnuja.hypersonic.model.AppModel;
import io.ibnuja.hypersonic.navigation.Route;
import io.ibnuja.hypersonic.navigation.ScreenFactory;
import io.ibnuja.hypersonic.state.App;
import io.ibnuja.hypersonic.trait.Dispatcher;
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

    private final AppModel appModel;
    private final ScreenFactory screenFactory;
    private final Dispatcher<App.Action> dispatcher;

    protected Settings settings;

    public MainWindow(Hypersonic.Application app) {
        this.appModel = new AppModel();
        this.dispatcher = (App.Action action) -> {
            log.debug("Dispatching Action: {}", action);
            List<App.Event> events = appModel.update(action);
            for (App.Event event : events) {
                handleEvent(event);
            }
        };
        this.screenFactory = new ScreenFactory(route -> dispatcher.dispatch(new App.Action.Navigate(route)));
        setApplication(app);
        updateNavigationRoot(new Route.Home());
        setupSidebar();
    }

    private void handleEvent(App.Event event) {
        log.debug("[MainWindow.screenFactory] Handling Event: {}", event);
        switch (event) {
            case App.Event.NavigationPushed(var route) -> {
                var page = screenFactory.create(route);
                navigationView.push(page);
                logStackLevel("After Push");
            }
            case App.Event.NavigationPopped _ -> {
                navigationView.pop();
                logStackLevel("After Pop");
            }
            case App.Event.NavigationReset(var route) -> {
                updateNavigationRoot(route);
                logStackLevel("After Reset");
            }
        }
    }

    private void updateNavigationRoot(Route route) {
        NavigationPage page = screenFactory.create(route);
        navigationView.replace(List.of(page).toArray(NavigationPage[]::new));

        if (splitView.getCollapsed()) {
            splitView.setShowContent(true);
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

    public void pop() {
        navigationView.pop();
    }

    private void setupSidebar() {
        List<SidebarItem> items = List.of(
                new SidebarItem("Home", new Route.Home(), "user-home-symbolic"),
                new SidebarItem("Now Playing", new Route.NowPlaying()),
                new SidebarItem("Albums", new Route.Album(), "library-album-symbolic"),
                new SidebarItem("Artists", new Route.Artists()),
                new SidebarItem("Songs", new Route.Songs(), "library-music-symbolic")
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
                            dispatcher.dispatch(new App.Action.ResetNavigation(i.route()));
                        });
            }
        });
    }
}
