package io.ibnuja.hypersonic;

import io.github.jwharm.javagi.base.GErrorException;
import io.github.jwharm.javagi.gobject.types.Types;
import io.ibnuja.hypersonic.ui.MainWindow;
import io.ibnuja.hypersonic.ui.components.playback.PlaybackControls;
import io.ibnuja.hypersonic.ui.components.playback.PlaybackInfo;
import io.ibnuja.hypersonic.ui.components.playback.PlaybackWidget;
import io.ibnuja.hypersonic.ui.components.selection.SelectionToolbar;
import io.ibnuja.hypersonic.ui.components.settings.SettingWindow;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.gnome.gio.ApplicationFlags;
import org.gnome.gio.File;
import org.gnome.gio.Resource;
import org.gnome.gio.SimpleAction;
import org.gnome.glib.List;
import org.gnome.glib.Variant;
import org.gnome.gtk.Window;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@SuppressWarnings("java:S1118")
public class Hypersonic {

    static void main(String[] args) throws GErrorException, IOException {
        try (InputStream in = Hypersonic.class.getResourceAsStream("/hypersonicapp.gresource")) {
            Types.register(PlaybackWidget.class);
            Types.register(PlaybackInfo.class);
            Types.register(PlaybackControls.class);
            Types.register(SelectionToolbar.class);
            Resource resource;
            if (in != null) {
                resource = Resource.fromData(in.readAllBytes());
            }
            else {
                resource = Resource.load("src/main/resources/hypersonicapp.gresource");
            }
            resource.resourcesRegister();
            new Application().run(args);
        } catch (IOException e) {
            log.error("error loading resource:", e);
        }
    }

    @SuppressWarnings("java:S110")
    public static class Application extends org.gnome.adw.Application {

        @Override
        public void activate() {
            MainWindow win;
            List<Window> windows = super.getWindows();
            if (!windows.isEmpty()) {
                win = (MainWindow) windows.getFirst();
            } else {
                win = new MainWindow(this);
            }

            win.present();
        }

        @Override
        public void open(File[] files, String hint) {
            MainWindow win;
            List<Window> windows = super.getWindows();
            if (!windows.isEmpty())
                win = (MainWindow) windows.getFirst();
            else
                win = new MainWindow(this);
            win.present();
        }

        public void preferencesActivated(Variant parameter) {
            MainWindow win = (MainWindow) getActiveWindow();
            // Assuming Settings class is in the component.settings package
            SettingWindow settingWindow = new SettingWindow();
            settingWindow.present(win);
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

        public Application() {
            setApplicationId("io.ibnuja.Hypersonic");
            setFlags(ApplicationFlags.HANDLES_OPEN);
        }
    }
}
