package io.ibnuja.hypersonic.audio;

import io.ibnuja.hypersonic.model.Song;
import io.ibnuja.hypersonic.state.PlayerState;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.gnome.glib.GLib;

@Slf4j
public class AudioPlayer {

    @Getter
    private final PlayerState state;
    private final GstBackend backend;

    // private final Queue queue;

    public AudioPlayer() {
        this.state = new PlayerState();
        this.backend = new GstBackend(this::send);
    }

    public void send(PlaybackAction action) {
        GLib.idleAdd(GLib.PRIORITY_DEFAULT_IDLE, () -> {
            processAction(action);
            return false;
        });
    }

    private void processAction(PlaybackAction action) {
        log.debug("Processing Action: {}", action);

        switch (action) {
            case PlaybackAction.Play play -> {
                state.setPlaying(true);
                backend.play();
            }
            case PlaybackAction.Pause pause -> {
                state.setPlaying(false);
                backend.pause();
            }
            case PlaybackAction.Stop stop -> {
                state.setPlaying(false);
                state.setPosition(0);
                backend.stop();
            }
            case PlaybackAction.LoadSong load -> {
                Song song = load.song();
                state.setCurrentSong(song);
                backend.setUri(song.getUri());
                // autoplay
                send(new PlaybackAction.Play());
            }
            case PlaybackAction.SongFinished songFinished -> {
                log.info("Song Finished");
                //play next
                send(new PlaybackAction.Stop());
            }
            case PlaybackAction.PositionChanged pos -> state.setPosition(pos.position());
            case null, default -> {
            }
        }
    }

    private boolean tick() {
        if (state.isPlaying()) {
            long pos = backend.queryPosition();
            if (pos >= 0 && pos != state.getPosition()) {
                state.setPosition(pos);
            }
        }
        return true;
    }
}
