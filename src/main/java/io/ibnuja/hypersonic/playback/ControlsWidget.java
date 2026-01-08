package io.ibnuja.hypersonic.playback;

import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.gnome.gtk.Box;
import org.gnome.gtk.Button;
import org.gnome.gtk.ToggleButton;
import org.javagi.gobject.annotations.InstanceInit;
import org.javagi.gtk.annotations.GtkChild;
import org.javagi.gtk.annotations.GtkTemplate;

import java.lang.foreign.MemorySegment;
import java.util.Objects;

@SuppressWarnings("java:S110")
@Slf4j
@EqualsAndHashCode(callSuper = true)
@GtkTemplate(name = "PlaybackControlsWidget", ui = "/io/ibnuja/Hypersonic/components/playback/playback_controls.ui")
public class ControlsWidget extends Box {

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

    @SuppressWarnings("unused")
    public ControlsWidget() {
        super();
    }

    @SuppressWarnings("unused")
    public ControlsWidget(MemorySegment address) {
        super(address);
    }

    @InstanceInit
    @SuppressWarnings({"unused", "java:S1186"})
    public void init() {
    }

    public void setup(PlayerState vm) {
        vm.<Boolean, String>bindProperty("playing", playPauseButton, "icon-name")
                .transformTo(playing -> {
                    log.trace("PlayerState playing state is changed to {}. Updating playPauseButton icon-name", playing);
                    if (Objects.equals(Boolean.TRUE, playing)) {
                        return "media-playback-pause-symbolic";
                    } else {
                        return "media-playback-start-symbolic";
                    }
                })
                .syncCreate()
                .build();

        playPauseButton.onClicked(vm::togglePlay);
    }
}
