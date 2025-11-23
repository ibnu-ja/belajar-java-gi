package io.ibnuja.hypersonic.ui;

import io.ibnuja.hypersonic.Hypersonic;
import io.ibnuja.hypersonic.model.AppModel;
import io.ibnuja.hypersonic.model.NavigationModel;
import io.ibnuja.hypersonic.navigation.Route;
import io.ibnuja.hypersonic.navigation.ScreenFactory;
import io.ibnuja.hypersonic.trait.Dispatcher;
import io.ibnuja.hypersonic.state.App;
import io.ibnuja.hypersonic.ui.components.sidebar.SidebarItem;
import io.ibnuja.hypersonic.ui.components.sidebar.SidebarRow;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.gnome.adw.ApplicationWindow;
import org.gnome.adw.NavigationSplitView;
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

    @GtkChild(name = "navigation_stack")
    public Stack navigationStack;

    @GtkChild(name = "split_view")
    public NavigationSplitView splitView;

    @GtkChild(name = "home_listbox")
    public ListBox homeListBox;

    private final AppModel appModel;
    private final NavigationModel navigationModel;
    private final ScreenFactory screenFactory;

    protected Settings settings;

    public MainWindow(Hypersonic.Application app) {
        setApplication(app);

        this.appModel = new AppModel();

        Dispatcher<App.Action> dispatcher = (App.Action action) -> {
            log.debug("Dispatching Action: {}", action);
            List<App.Event> events = appModel.update(action);
            for (App.Event event : events) {
                handleEvent(event);
            }
        };
        this.navigationModel = new NavigationModel(appModel.getAppState(), dispatcher);
        this.screenFactory = new ScreenFactory(route -> dispatcher.dispatch(new App.Action.Navigate(route)));
        updateNavigationStack(new Route.Home());
        setupSidebar();
    }

    private void handleEvent(App.Event event) {
        log.debug("Handling Event: {}", event);

        switch (event) {
            case App.Event.NavigationPushed(var route) -> updateNavigationStack(route);
            case App.Event.NavigationPopped _ -> {
                Route current = appModel.getAppState().getCurrentRoute();
                if (current != null) {
                    navigationStack.setVisibleChildName(current.name());
                }
            }
            case App.Event.NavigationReset(var root) -> {
                Widget child = navigationStack.getFirstChild();
                while (child != null) {
                    Widget next = child.getNextSibling();
                    navigationStack.remove(child);
                    child = next;
                }
                updateNavigationStack(root);
            }
        }
    }

    private void updateNavigationStack(Route route) {
        String id = route.name();
        Widget view = navigationStack.getChildByName(id);

        if (view == null) {
            view = screenFactory.create(route);
            if (view != null) {
                navigationStack.addNamed(view, id);
            }
        }

        navigationStack.setVisibleChildName(id);
        if (splitView.getCollapsed()) {
            splitView.setShowContent(true);
        }
    }

    @InstanceInit
    @SuppressWarnings("unused")
    public void init() {
        settings = new Settings("io.ibnuja.Hypersonic");
    }

    public void pop() {
        navigationModel.pop();
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
                        .ifPresent(i -> navigationModel.resetTo(i.route()));
            }
        });
    }
}
