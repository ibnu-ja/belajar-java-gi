package io.ibnuja.hypersonic;

import io.ibnuja.hypersonic.audio.AudioPlayer;
import io.ibnuja.hypersonic.audio.PlaybackAction;
import io.ibnuja.hypersonic.model.Song;
import io.ibnuja.hypersonic.state.ConnectionState;
import io.ibnuja.hypersonic.ui.MainWindow;
import io.ibnuja.hypersonic.ui.components.playback.PlaybackControlsWidget;
import io.ibnuja.hypersonic.ui.components.playback.PlaybackInfoWidget;
import io.ibnuja.hypersonic.ui.components.playback.PlaybackWidget;
import io.ibnuja.hypersonic.ui.components.selection.SelectionToolbarWidget;
import io.ibnuja.hypersonic.ui.components.settings.SettingWindow;
import lombok.extern.slf4j.Slf4j;
import org.freedesktop.gstreamer.gst.Gst;
import org.gnome.gdkpixbuf.Pixbuf;
import org.gnome.gio.ApplicationFlags;
import org.gnome.gio.File;
import org.gnome.gio.Resource;
import org.gnome.gio.SimpleAction;
import org.gnome.glib.Variant;
import org.gnome.gtk.Window;
import org.javagi.base.GErrorException;
import org.javagi.base.Out;
import org.javagi.gtk.types.TemplateTypes;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@SuppressWarnings({"java:S1118", "java:S125"})
public class Hypersonic {

    // Global reference to AudioPlayer (The Controller)
    public static AudioPlayer audioPlayer;

    public static void main(String[] args) throws GErrorException {
        // GStreamer initialization with Out<String[]> as requested
        // We pass the args to GStreamer so it can parse its own flags if needed
        Out<String[]> gstArgs = new Out<>(args);
        Gst.init(gstArgs);

        Pixbuf.getFormats().forEach(pixbufFormat -> log.debug(
                "pixbufFormat supported: {}, {} ",
                pixbufFormat.getName(),
                pixbufFormat.getDescription()
        ));

        // Initialize the Audio Controller
        audioPlayer = new AudioPlayer();

        // Connect to Subsonic (Hardcoded for now as per your setup)
        ConnectionState.INSTANCE.connect("http://demo.subsonic.org", "guest", "guest");

        // Test: Load a random song immediately
        try {
            var result = ConnectionState.INSTANCE.getApi().getRandomSongs(1);
            if (!result.getRandomSongs().getSong().isEmpty()) {
                var apiSong = result.getRandomSongs().getSong().getFirst();
                var streamUrl = ConnectionState.INSTANCE.getApi().streamUrl(apiSong.getId());

                // Convert Subsonic Song to our GObject Song
                Song song = new Song(
                        apiSong.getId(),
                        apiSong.getTitle(),
                        apiSong.getArtist(),
                        streamUrl,
                        apiSong.getDuration() != null ? apiSong.getDuration() : 0
                );

                // Send Action: Load Song
                audioPlayer.send(new PlaybackAction.LoadSong(song));
            }
        } catch (Exception e) {
            log.error("Failed to fetch songs", e);
        }

        // Load Resources and Start Application
        try (InputStream in = Hypersonic.class.getResourceAsStream("/hypersonicapp.gresource")) {
            // Register Template Classes
            TemplateTypes.register(PlaybackWidget.class);
            TemplateTypes.register(PlaybackInfoWidget.class);
            TemplateTypes.register(PlaybackControlsWidget.class);
            TemplateTypes.register(SelectionToolbarWidget.class);

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
