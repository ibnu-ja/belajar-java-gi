package io.ibnuja.hypersonic;

import io.ibnuja.hypersonic.ui.MainWindow;
import io.ibnuja.hypersonic.ui.components.playback.PlaybackControlsWidget;
import io.ibnuja.hypersonic.ui.components.playback.PlaybackInfoWidget;
import io.ibnuja.hypersonic.ui.components.playback.PlaybackWidget;
import io.ibnuja.hypersonic.ui.components.selection.SelectionToolbarWidget;
import io.ibnuja.hypersonic.ui.components.settings.SettingWindow;
import lombok.extern.slf4j.Slf4j;
import org.gnome.gio.ApplicationFlags;
import org.gnome.gio.File;
import org.gnome.gio.Resource;
import org.gnome.gio.SimpleAction;
import org.gnome.glib.Variant;
import org.gnome.gtk.Window;
import org.javagi.base.GErrorException;
import org.javagi.gtk.types.TemplateTypes;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@SuppressWarnings("java:S1118")
public class Hypersonic {

    static void main(String[] args) throws GErrorException {
        try (InputStream in = Hypersonic.class.getResourceAsStream("/hypersonicapp.gresource")) {
            TemplateTypes.register(PlaybackWidget.class);
            TemplateTypes.register(PlaybackInfoWidget.class);
            TemplateTypes.register(PlaybackControlsWidget.class);
            TemplateTypes.register(SelectionToolbarWidget.class);
            Resource resource;
            if (in != null) {
                resource = Resource.fromData(in.readAllBytes());
            } else {
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
