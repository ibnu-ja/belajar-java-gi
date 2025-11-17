package io.ibnuja;

import io.github.jwharm.javagi.gobject.annotations.InstanceInit;
import io.github.jwharm.javagi.gtk.annotations.GtkChild;
import io.github.jwharm.javagi.gtk.annotations.GtkTemplate;
import lombok.EqualsAndHashCode;
import org.gnome.adw.ApplicationWindow;
import org.gnome.adw.Toast;
import org.gnome.adw.ToastOverlay;
import org.gnome.gio.Settings;
import org.gnome.gtk.GtkBuilder;
import org.gnome.gtk.MenuButton;

@GtkTemplate(ui = "/io/ibnuja/hypersonic/window.ui")
@SuppressWarnings({"java:S110", "java:S112"})
@EqualsAndHashCode(callSuper = true)
public class HypersonicMainWindow extends ApplicationWindow {

    @GtkChild
    public MenuButton hamburger;

    @GtkChild(name = "toast_overlay")
    public ToastOverlay toastOverlay;

    protected Settings settings;

    public HypersonicMainWindow(HypersonicApp app) {
        setApplication(app);
    }

    @InstanceInit
    @SuppressWarnings("unused")
    public void init() {
        var builder = GtkBuilder.fromResource("/io/ibnuja/hypersonic/menu.ui");
        var menu = (org.gnome.gio.MenuModel) builder.getObject("settings");
        hamburger.setMenuModel(menu);

        settings = new Settings("io.ibnuja.Hypersonic");
    }

    @SuppressWarnings("unused")
    private void showToast(String message) {
        Toast toast = new Toast(message);
        toastOverlay.addToast(toast);
    }
}