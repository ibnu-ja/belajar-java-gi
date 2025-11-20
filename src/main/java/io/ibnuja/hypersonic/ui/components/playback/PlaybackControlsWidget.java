package io.ibnuja.hypersonic.ui.components.playback;

import lombok.EqualsAndHashCode;
import org.gnome.gtk.Box;
import org.javagi.gtk.annotations.GtkTemplate;

import java.lang.foreign.MemorySegment;

@SuppressWarnings("java:S110")
@EqualsAndHashCode(callSuper = true)
@GtkTemplate(name = "PlaybackControlsWidget", ui = "/io/ibnuja/hypersonic/components/playback/playback_controls.ui")
public class PlaybackControlsWidget extends Box {

    public PlaybackControlsWidget() {
        super();
    }

    public PlaybackControlsWidget(MemorySegment address) {
        super(address);
    }
}
