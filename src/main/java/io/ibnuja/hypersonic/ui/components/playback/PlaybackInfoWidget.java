package io.ibnuja.hypersonic.ui.components.playback;

import io.ibnuja.hypersonic.state.PlaybackState;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.gnome.gtk.Button;
import org.gnome.gtk.Label;
import org.javagi.gobject.annotations.InstanceInit;
import org.javagi.gtk.annotations.GtkChild;
import org.javagi.gtk.annotations.GtkTemplate;

import java.lang.foreign.MemorySegment;

@Slf4j
@SuppressWarnings("java:S110")
@EqualsAndHashCode(callSuper = true)
@GtkTemplate(name = "PlaybackInfoWidget", ui = "/io/ibnuja/hypersonic/components/playback/playback_info.ui")
public class PlaybackInfoWidget extends Button {

    @GtkChild(name = "current_song_info")
    public Label currentSongInfo;

    public PlaybackInfoWidget() {
        super();
    }

    public PlaybackInfoWidget(MemorySegment address) {
        super(address);
    }

    @InstanceInit
    public void init() {
        PlaybackState store = PlaybackState.getInstance();

        store.subscribe(status -> {
            switch (status) {
                case PAUSED -> currentSongInfo.setText("Paused");
                case PLAYING -> currentSongInfo.setText("Playing");
                // case STOPPED -> currentSongInfo.setText("Stopped");
                default -> currentSongInfo.setText("No song playing.");
            }
        });
    }
}
