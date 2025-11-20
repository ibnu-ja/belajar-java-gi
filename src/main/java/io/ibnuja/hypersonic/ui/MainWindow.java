package io.ibnuja.hypersonic.ui;

import io.ibnuja.hypersonic.Hypersonic;
import lombok.EqualsAndHashCode;
import org.gnome.adw.ApplicationWindow;
import org.gnome.adw.Toast;
import org.gnome.gio.Settings;
import org.javagi.gobject.annotations.InstanceInit;
import org.javagi.gtk.annotations.GtkTemplate;

@GtkTemplate(ui = "/io/ibnuja/hypersonic/window.ui", name = "MainWindow")
@SuppressWarnings({"java:S110", "java:S112", "java:S125"})
@EqualsAndHashCode(callSuper = true)
public class MainWindow extends ApplicationWindow {

    //@GtkChild(name = "toast_overlay")
    //public ToastOverlay toastOverlay;

    protected Settings settings;

    public MainWindow(Hypersonic.Application app) {
        setApplication(app);
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
}
