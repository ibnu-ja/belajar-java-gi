package io.ibnuja.hypersonic.ui.components.playback;

import io.github.jwharm.javagi.gobject.annotations.RegisteredType;
import io.github.jwharm.javagi.gtk.annotations.GtkTemplate;
import lombok.EqualsAndHashCode;
import org.gnome.gtk.Box;

import java.lang.foreign.MemorySegment;

@EqualsAndHashCode(callSuper = true)
@GtkTemplate(ui = "/io/ibnuja/hypersonic/components/playback/playback_controls.ui", name = "PlaybackControlsWidget")
@RegisteredType(name = "PlaybackControlsWidget")
public class PlaybackControls extends Box {

    public PlaybackControls() {
        super();
    }

    public PlaybackControls(MemorySegment address) {
        super(address);
    }
}
