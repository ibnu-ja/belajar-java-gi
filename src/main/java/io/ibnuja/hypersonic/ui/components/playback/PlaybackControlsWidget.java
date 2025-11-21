package io.ibnuja.hypersonic.ui.components.playback;

import io.ibnuja.hypersonic.Hypersonic;
import io.ibnuja.hypersonic.audio.AudioPlayer;
import io.ibnuja.hypersonic.audio.PlaybackAction;
import io.ibnuja.hypersonic.state.PlayerState;
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
        AudioPlayer player = Hypersonic.audioPlayer;
        PlayerState state = player.getState();

        playPauseButton.onClicked(() -> {
            if (state.isPlaying()) {
                playPauseButton.setIconName("media-playback-start-symbolic");
                player.send(new PlaybackAction.Pause());
            } else {
                playPauseButton.setIconName("media-playback-pause-symbolic");
                player.send(new PlaybackAction.Play());
            }
        });
    }
}
