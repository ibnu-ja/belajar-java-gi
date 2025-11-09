package io.ibnuja;

import io.github.jwharm.javagi.base.GErrorException;
import io.github.jwharm.javagi.base.Out;
import io.github.jwharm.javagi.gobject.annotations.InstanceInit;
import io.github.jwharm.javagi.gtk.annotations.GtkChild;
import io.github.jwharm.javagi.gtk.annotations.GtkTemplate;
import lombok.EqualsAndHashCode;
import org.gnome.adw.ApplicationWindow;
import org.gnome.adw.Toast;
import org.gnome.adw.ToastOverlay;
import org.gnome.adw.ViewStack;
import org.gnome.gio.Settings;
import org.gnome.gtk.*;

@GtkTemplate(ui = "/io/ibnuja/hypersonic/window.ui")
@SuppressWarnings({"java:S110", "java:S112"})
@EqualsAndHashCode(callSuper = true)
public class HypersonicMainWindow extends ApplicationWindow {

    @GtkChild(name = "view_stack")
    public ViewStack viewStack;

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
        // settings.bind("transition", viewStack, "enable-transitions", SettingsBindFlags.DEFAULT);
    }

    public void open(org.gnome.gio.File file) {
        String basename = file.getBasename();

        var view = new TextView();
        view.setEditable(false);
        view.setCursorVisible(false);

        var scrolled = new ScrolledWindow();
        scrolled.setHexpand(true);
        scrolled.setVexpand(true);
        scrolled.setChild(view);
        viewStack.addTitled(scrolled, basename, basename);

        var buffer = view.getBuffer();
        try {
            var contents = new Out<byte[]>();
            if (file.loadContents(null, contents, null)) {
                String str = new String(contents.get());
                buffer.setText(str, str.length());
            }
        } catch (GErrorException e) {
            throw new RuntimeException(e);
        }

        var tag = buffer.createTag(null, null);
        settings.bind("font", tag, "font", org.gnome.gio.SettingsBindFlags.DEFAULT);
        TextIter startIter = new TextIter();
        TextIter endIter = new TextIter();
        buffer.getStartIter(startIter);
        buffer.getEndIter(endIter);
        buffer.applyTag(tag, startIter, endIter);
        showToast("Loaded file: " + basename);
    }

    private void showToast(String message) {
        Toast toast = new Toast(message);
        toastOverlay.addToast(toast);
    }
}