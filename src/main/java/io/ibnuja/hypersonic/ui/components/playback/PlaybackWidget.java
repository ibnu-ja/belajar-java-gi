package io.ibnuja.hypersonic.ui.components.playback;

import io.github.jwharm.javagi.gobject.annotations.RegisteredType;
import io.github.jwharm.javagi.gtk.annotations.GtkChild;
import io.github.jwharm.javagi.gtk.annotations.GtkTemplate;
import lombok.EqualsAndHashCode;
import org.gnome.gtk.Box;
import org.gnome.gtk.Label;
import org.gnome.gtk.Scale;

import java.lang.foreign.MemorySegment;

@EqualsAndHashCode(callSuper = true)
@GtkTemplate(ui = "/io/ibnuja/hypersonic/components/playback/playback_widget.ui", name = "PlaybackWidget")
//@RegisteredType(name = "PlaybackWidget")
public class PlaybackWidget extends Box {

    @GtkChild(name = "seek_bar")
    Scale seekBar;

    @GtkChild(name = "track_position")
    Label trackPosition;

    @GtkChild(name = "track_duration")
    Label trackDuration;

    //@GtkChild(name = "now_playing")
    //public PlaybackInfo nowPlaying;

    //@GtkChild(name = "controls")
    //public PlaybackControls controls;

    @GtkChild(name = "volume_slider")
    Scale volumeSlider;

    public PlaybackWidget() {
        super();
    }

    public PlaybackWidget(MemorySegment address) {
        super(address);
    }
}
