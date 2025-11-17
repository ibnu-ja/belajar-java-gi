package io.ibnuja;

import org.gnome.gio.ApplicationFlags;
import org.gnome.gio.File;
import org.gnome.gio.SimpleAction;
import org.gnome.glib.List;
import org.gnome.glib.Variant;
import org.gnome.adw.Application;
import org.gnome.gtk.Window;

@SuppressWarnings("java:S110")
public class HypersonicApp extends Application {

    @Override
    public void activate() {
        HypersonicMainWindow win = new HypersonicMainWindow(this);
        win.present();
    }

    @Override
    public void open(File[] files, String hint) {
        HypersonicMainWindow win;
        List<Window> windows = super.getWindows();
        if (!windows.isEmpty())
            win = (HypersonicMainWindow) windows.getFirst();
        else
            win = new HypersonicMainWindow(this);
        win.present();
    }

    public void preferencesActivated(Variant parameter) {
        HypersonicMainWindow win = (HypersonicMainWindow) getActiveWindow();
        HypersonicSettings settings = new HypersonicSettings();
        settings.present(win);
    }

    public void quitActivated(Variant parameter) {
        super.quit();
    }

    @Override
    public void startup() {
        super.startup();

        var preferences = new SimpleAction("preferences", null);
        preferences.onActivate(this::preferencesActivated);
        addAction(preferences);

        var quit = new SimpleAction("quit", null);
        quit.onActivate(this::quitActivated);
        addAction(quit);

        String[] quitAccels = new String[]{"<Ctrl>q"};
        setAccelsForAction("app.quit", quitAccels);
    }

    public HypersonicApp() {
        setApplicationId("io.ibnuja.Hypersonic");
        setFlags(ApplicationFlags.HANDLES_OPEN);
    }
}
