package io.ibnuja.hypersonic.state;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.gnome.glib.GLib;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
public enum PlaybackState {
    INSTANCE;

    public enum Status {
        PLAYING,
        PAUSED
        //STOPPED
    }

    @Getter
    private Status status = Status.PAUSED;

    private final List<Consumer<Status>> listeners = new ArrayList<>();

    public static PlaybackState getInstance() {
        return INSTANCE;
    }

    public void togglePlay() {
        if (status == Status.PLAYING) {
            pause();
        } else {
            play();
        }
    }

    public void play() {
        log.info("Action: Play");
        status = Status.PLAYING;
        // TODO: Call GStreamer.play()
        notifyListeners();
    }

    public void pause() {
        log.info("Action: Pause");
        status = Status.PAUSED;
        // TODO: Call GStreamer.pause()
        notifyListeners();
    }

    public void stop() {
        log.info("Action: Stop");
        //status = Status.STOPPED;
        // TODO: Call GStreamer.stop()
        notifyListeners();
    }

    public void subscribe(Consumer<Status> listener) {
        listeners.add(listener);
        // set UI to initial state
        listener.accept(status);
    }

    private void notifyListeners() {
        GLib.idleAdd(
                GLib.PRIORITY_DEFAULT_IDLE, () -> {
                    listeners.forEach(listener -> listener.accept(status));
                    return false;
                }
        );
    }
}
