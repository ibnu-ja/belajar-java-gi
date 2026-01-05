package io.ibnuja.hypersonic;

import io.ibnuja.hypersonic.service.audio.AudioPlayer;
import io.ibnuja.hypersonic.model.AppModel;
import io.ibnuja.hypersonic.navigation.Route;
import io.ibnuja.hypersonic.state.App;
import io.ibnuja.hypersonic.service.api.ConnectionState;
import io.ibnuja.hypersonic.state.Playback;
import io.ibnuja.hypersonic.ui.MainWindow;
import io.ibnuja.hypersonic.ui.components.playback.PlaybackControlsWidget;
import io.ibnuja.hypersonic.ui.components.playback.PlaybackInfoWidget;
import io.ibnuja.hypersonic.ui.components.playback.PlaybackWidget;
import io.ibnuja.hypersonic.ui.components.selection.SelectionToolbarWidget;
import io.ibnuja.hypersonic.ui.components.settings.SettingWindow;
import io.ibnuja.hypersonic.ui.components.sidebar.SidebarRow;
import lombok.extern.slf4j.Slf4j;
import org.freedesktop.gstreamer.gst.Gst;
import org.gnome.gdk.Display;
import org.gnome.gdkpixbuf.Pixbuf;
import org.gnome.gio.ApplicationFlags;
import org.gnome.gio.File;
import org.gnome.gio.Resource;
import org.gnome.gio.SimpleAction;
import org.gnome.glib.Variant;
import org.gnome.gtk.IconTheme;
import org.gnome.gtk.Window;
import org.javagi.base.GErrorException;
import org.javagi.base.Out;
import org.javagi.gtk.types.TemplateTypes;
import org.javagi.util.Intl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@SuppressWarnings({"java:S1118", "java:S125"})
public class Hypersonic {

    @SuppressWarnings({"java:S1444", "java:S1104", "java:S1135"})
    //TODO use a proper singleton pattern
    public static AudioPlayer audioPlayer;

    public static final AppModel appModel = new AppModel();

    public static void navigate(Route route) {
        appModel.dispatch(new App.Action.Navigate(route));
    }

    @SuppressWarnings("unused")
    public static void back() {
        appModel.dispatch(new App.Action.NavigateBack());
    }

    static void main(String[] args) throws GErrorException {
        LoggingBootstrap.init();
        Out<String[]> gstArgs = new Out<>(args);
        Gst.init(gstArgs);

        Pixbuf.getFormats().forEach(pixbufFormat -> log.debug(
                "pixbufFormat supported: {}, {} ",
                pixbufFormat.getName(),
                pixbufFormat.getDescription()
        ));

        audioPlayer = new AudioPlayer();

        ConnectionState.INSTANCE.connect("http://demo.subsonic.org", "guest", "guest");

        String appId = "hypersonic";
        Intl.bindtextdomain(appId, Config.LOCALE_DIR);

        Intl.textdomain(appId);
        ConnectionState.INSTANCE.getApi()
                .getRandomSongs(1)
                .thenAccept(result -> {
                    if (!result.getRandomSongs().getSong().isEmpty()) {
                        var song = result.getRandomSongs().getSong().getFirst();
                        log.info("Loaded song: {}", song);
                        audioPlayer.dispatch(new Playback.Action.LoadSongs(List.of(song)));
                    }
                })
                .exceptionally(e -> {
                    log.error("Failed to fetch songs", e);
                    return null;
                });

        try (InputStream in = Hypersonic.class.getResourceAsStream("/hypersonicapp.gresource")) {
            // Register Template Classes
            TemplateTypes.register(PlaybackWidget.class);
            TemplateTypes.register(PlaybackInfoWidget.class);
            TemplateTypes.register(PlaybackControlsWidget.class);
            TemplateTypes.register(SelectionToolbarWidget.class);
            TemplateTypes.register(SidebarRow.class);

            Resource resource;
            if (in != null) {
                resource = Resource.fromData(in.readAllBytes());
            } else {
                // Fallback for dev environment if needed
                resource = Resource.load("src/main/resources/hypersonicapp.gresource");
            }
            resource.resourcesRegister();

            new Application().run(gstArgs.get());

        } catch (IOException e) {
            log.error("error loading resource:", e);
        } finally {
            ConnectionState.INSTANCE.disconnect();
            log.info("Application exited and connections closed.");
        }
    }

    @SuppressWarnings("java:S110")
    public static class Application extends org.gnome.adw.Application {

        @Override
        public void activate() {
            Display display = Display.getDefault();
            if (display != null) {
                IconTheme theme = IconTheme.getForDisplay(display);
                theme.addResourcePath("/io/ibnuja/Hypersonic/icons");
            } else {
                log.error("Display.getDefault() returned null inside activate()!");
            }

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
