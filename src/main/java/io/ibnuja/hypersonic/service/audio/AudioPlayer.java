package io.ibnuja.hypersonic.service.audio;

import io.ibnuja.hypersonic.service.api.ConnectionState;
import io.ibnuja.hypersonic.state.Playback;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.gnome.glib.GLib;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class AudioPlayer {

    @Getter
    private final Playback state;
    private final GstBackend backend;
    private final List<Consumer<Playback.Event>> listeners = new ArrayList<>();

    public AudioPlayer() {
        this.state = new Playback();
        this.backend = new GstBackend(this::dispatch);
    }

    public void subscribe(Consumer<Playback.Event> listener) {
        listeners.add(listener);
    }

    public void dispatch(Playback.Action action) {
        GLib.idleAdd(GLib.PRIORITY_DEFAULT_IDLE, () -> {
            var events = state.update(action);
            for (var event : events) {
                // handle side effects
                processInternalEvent(event);
                // notify listeners
                listeners.forEach(l -> l.accept(event));
            }
            return false;
        });
    }

    private void processInternalEvent(Playback.Event event) {
        switch (event) {
            case Playback.Event.TrackChanged(var song) -> {
                var uri = ConnectionState.INSTANCE.getApi().streamUrl(song.getId());
                backend.setUri(uri);
                backend.play();
            }
            case Playback.Event.PlaybackResumed _ -> backend.play();
            case Playback.Event.PlaybackPaused _ -> backend.pause();
            case Playback.Event.PlaybackStopped _ -> backend.stop();
            default -> log.warn("Unhandled event: {}. Do nothing", event);
        }
    }
}
