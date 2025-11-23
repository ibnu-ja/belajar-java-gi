package io.ibnuja.hypersonic.ui;

import io.ibnuja.hypersonic.Hypersonic;
import io.ibnuja.hypersonic.navigation.Route;
import io.ibnuja.hypersonic.navigation.ScreenFactory;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.gnome.adw.ApplicationWindow;
import org.gnome.adw.NavigationSplitView;
import org.gnome.adw.Toast;
import org.gnome.gio.Settings;
import org.gnome.gtk.Stack;
import org.gnome.gtk.Widget;
import org.javagi.gobject.annotations.InstanceInit;
import org.javagi.gtk.annotations.GtkChild;
import org.javagi.gtk.annotations.GtkTemplate;

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

    private final ScreenFactory screenFactory;

    protected Settings settings;

    public MainWindow(Hypersonic.Application app) {
        setApplication(app);

        this.screenFactory = new ScreenFactory();
        navigate(new Route.Home());
        log.info("homepage loaded");
    }

    @InstanceInit
    @SuppressWarnings("unused")
    public void init() {
        settings = new Settings("io.ibnuja.Hypersonic");
    }

    @SuppressWarnings("unused")
    private void showToast(String message) {
        Toast toast = new Toast(message);
        //toastOverlay.addToast(toast);
    }

    public void navigate(Route route) {
        String id = route.name();
        if (navigationStack.getChildByName(id) == null) {
            Widget view = screenFactory.create(route);
            navigationStack.addNamed(view, id);
        }
        navigationStack.setVisibleChildName(id);
        if (splitView.getCollapsed()) {
            splitView.setShowContent(true);
        }
    }
}
