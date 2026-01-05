package io.ibnuja.hypersonic.playback;

import lombok.EqualsAndHashCode;
import org.gnome.gtk.Box;
import org.gnome.gtk.Label;
import org.gnome.gtk.Scale;
import org.javagi.gtk.annotations.GtkChild;
import org.javagi.gtk.annotations.GtkTemplate;

import java.lang.foreign.MemorySegment;

@SuppressWarnings("java:S110")
@EqualsAndHashCode(callSuper = true)
@GtkTemplate(name = "PlaybackWidget", ui = "/io/ibnuja/Hypersonic/components/playback/playback_widget.ui")
public class PlaybackWidget extends Box {

    @GtkChild
    public PlaybackControlsWidget controls;

    @GtkChild(name = "now_playing")
    public PlaybackInfoWidget nowPlaying;

    @GtkChild(name = "seek_bar")
    public Scale seekBar;

    @GtkChild(name = "track_duration")
    public Label trackDuration;

    @GtkChild(name = "track_position")
    public Label trackPosition;

    @GtkChild(name = "volume_slider")
    public Scale volumeSlider;

    public PlaybackWidget() {
        super();
    }

    public PlaybackWidget(MemorySegment address) {
        super(address);
    }
}
