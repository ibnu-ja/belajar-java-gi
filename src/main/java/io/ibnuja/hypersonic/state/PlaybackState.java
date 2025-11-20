package io.ibnuja.hypersonic.state;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.gnome.glib.GLib;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class PlaybackState {

    private static final PlaybackState INSTANCE = new PlaybackState();

    @Getter
    private boolean isPlaying = false;

    private final List<Consumer<Boolean>> listeners = new ArrayList<>();

    public static PlaybackState getInstance() {
        return INSTANCE;
    }

    public void togglePlay() {
        log.info("Toggle play button pressed");
        isPlaying = !isPlaying;
        // call GStreamer/Subsonic API here
        notifyListeners(); // <--- ADD THIS LINE
    }

    public void subscribe(Consumer<Boolean> listener) {
        listeners.add(listener);
    }

    private void notifyListeners() {
        GLib.idleAdd(GLib.PRIORITY_DEFAULT_IDLE, () -> {
            listeners.forEach(listener -> listener.accept(isPlaying));
            return false;
        });
    }
}
