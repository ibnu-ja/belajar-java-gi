package io.ibnuja.hypersonic.ui.components.playback;

import io.github.jwharm.javagi.gobject.annotations.RegisteredType;
import io.github.jwharm.javagi.gtk.annotations.GtkTemplate;
import lombok.EqualsAndHashCode;
import org.gnome.gtk.Button;

import java.lang.foreign.MemorySegment;

@EqualsAndHashCode(callSuper = true)
@GtkTemplate(ui = "/io/ibnuja/hypersonic/components/playback/playback_info.ui", name = "PlaybackInfoWidget")
@RegisteredType(name = "PlaybackInfoWidget")
public class PlaybackInfo extends Button {

    public PlaybackInfo() {
        super();
    }

    public PlaybackInfo(MemorySegment address) {
        super(address);
    }
}
