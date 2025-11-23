package io.ibnuja.hypersonic.ui.components.playback;

import io.ibnuja.hypersonic.Hypersonic;
import io.ibnuja.hypersonic.state.Playback;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.gnome.gtk.Box;
import org.gnome.gtk.Button;
import org.gnome.gtk.Image;
import org.gnome.gtk.ToggleButton;
import org.javagi.gobject.annotations.InstanceInit;
import org.javagi.gtk.annotations.GtkChild;
import org.javagi.gtk.annotations.GtkTemplate;
import org.jetbrains.annotations.NotNull;

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
    public Button repeatButton;

    public PlaybackControlsWidget() {
        super();
    }

    public PlaybackControlsWidget(MemorySegment address) {
        super(address);
    }

    @InstanceInit
    @SuppressWarnings("unused")
    public void init() {
        Hypersonic.audioPlayer.subscribe(this::onEvent);

        playPauseButton.onClicked(() -> Hypersonic.audioPlayer.dispatch(new Playback.Action.TogglePlay()));

        nextButton.onClicked(() -> Hypersonic.audioPlayer.dispatch(new Playback.Action.Next()));

        prevButton.onClicked(() -> Hypersonic.audioPlayer.dispatch(new Playback.Action.Previous()));

        shuffleButton.onToggled(() -> Hypersonic.audioPlayer.dispatch(new Playback.Action.SetShuffle(shuffleButton.getActive())));

        repeatButton.onClicked(() -> Hypersonic.audioPlayer.dispatch(new Playback.Action.ToggleRepeat()));
    }

    private void onEvent(@NotNull Playback.Event event) {
        log.debug("playback event: {}", event);
        switch (event) {
            //noinspection unused
            case Playback.Event.PlaybackResumed resumed -> {
                //TODO add playingImage.setFromPaintable();
                playPauseButton.setIconName("media-playback-pause-symbolic");
            }

            case Playback.Event.PlaybackPaused _, Playback.Event.PlaybackStopped _ -> {
                //TODO add playingImage.setFromPaintable();
                playPauseButton.setIconName("media-playback-start-symbolic");
            }

            case Playback.Event.ShuffleChanged e -> shuffleButton.setActive(e.enabled());

            default -> {
            }
        }
    }
}
