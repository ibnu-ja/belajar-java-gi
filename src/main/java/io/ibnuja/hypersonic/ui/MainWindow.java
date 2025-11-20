package io.ibnuja.hypersonic.ui;

import io.github.jwharm.javagi.gobject.annotations.InstanceInit;
import io.github.jwharm.javagi.gtk.annotations.GtkChild;
import io.github.jwharm.javagi.gtk.annotations.GtkTemplate;
import io.ibnuja.hypersonic.Hypersonic;
import io.ibnuja.hypersonic.ui.components.sidebar.Sidebar;
import lombok.EqualsAndHashCode;
import org.gnome.adw.ApplicationWindow;
import org.gnome.adw.NavigationSplitView;
import org.gnome.adw.Toast;
import org.gnome.adw.ToastOverlay;
import org.gnome.gio.Settings;

@GtkTemplate(ui = "/io/ibnuja/hypersonic/window.ui", name = "MainWindow")
@SuppressWarnings({"java:S110", "java:S112"})
@EqualsAndHashCode(callSuper = true)
public class MainWindow extends ApplicationWindow {

    //@GtkChild(name = "toast_overlay")
    //public ToastOverlay toastOverlay;
    //
    //@GtkChild(name = "split_view")
    //public NavigationSplitView navigationSplitView;

    protected Settings settings;

    public MainWindow(Hypersonic.Application app) {
        setApplication(app);
    }

    @InstanceInit
    @SuppressWarnings("unused")
    public void init() {
        //navigationSplitView.setSidebar(new Sidebar());

        settings = new Settings("io.ibnuja.Hypersonic");
    }

    @SuppressWarnings("unused")
    private void showToast(String message) {
        Toast toast = new Toast(message);
        //toastOverlay.addToast(toast);
    }
}
