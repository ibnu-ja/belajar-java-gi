package io.ibnuja.hypersonic.ui.components.playback;

import lombok.EqualsAndHashCode;
import org.gnome.gtk.Button;
import org.gnome.gtk.Label;
import org.javagi.gtk.annotations.GtkChild;
import org.javagi.gtk.annotations.GtkTemplate;

import java.lang.foreign.MemorySegment;

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
}
