package io.ibnuja.hypersonic.ui.components.playback;

import io.ibnuja.hypersonic.state.PlaybackState;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.gnome.gtk.Box;
import org.gnome.gtk.Button;
import org.gnome.gtk.ToggleButton;
import org.javagi.gobject.annotations.InstanceInit;
import org.javagi.gtk.annotations.GtkChild;
import org.javagi.gtk.annotations.GtkTemplate;

import java.lang.foreign.MemorySegment;

@SuppressWarnings("java:S110")
@Slf4j
@EqualsAndHashCode(callSuper = true)
@GtkTemplate(name = "PlaybackControlsWidget", ui = "/io/ibnuja/hypersonic/components/playback/playback_controls.ui")
public class PlaybackControlsWidget extends Box {

    @GtkChild(name = "shuffle")
    public ToggleButton shuffleButton;

    @GtkChild(name = "prev")
    public Button prevButton;

    @GtkChild(name = "play_pause")
    public Button playPauseButton;

    @GtkChild(name = "next")
    public Button nextButton;

    @GtkChild(name = "repeat")
    public ToggleButton repeatButton;

    public PlaybackControlsWidget() {
        super();
    }

    public PlaybackControlsWidget(MemorySegment address) {
        super(address);
    }

    @InstanceInit
    public void init() {
        PlaybackState store = PlaybackState.getInstance();
        //    howto use
        playPauseButton.onClicked(store::togglePlay);

        store.subscribe(isPlaying -> {
            if (isPlaying) {
                playPauseButton.setIconName("media-playback-pause-symbolic");
                log.info("icon is changed to play");
            } else {
                playPauseButton.setIconName("media-playback-start-symbolic");
                log.info("icon is changed to pause");
            }
        });
    }
}
