package io.ibnuja.hypersonic.ui.components.playback;

import org.javagi.gtk.annotations.GtkTemplate;
import lombok.EqualsAndHashCode;
import org.gnome.gtk.Button;

import java.lang.foreign.MemorySegment;

@SuppressWarnings("java:S110")
@EqualsAndHashCode(callSuper = true)
@GtkTemplate(name = "PlaybackInfoWidget", ui = "/io/ibnuja/hypersonic/components/playback/playback_info.ui")
public class PlaybackInfoWidget extends Button {

    public PlaybackInfoWidget() {
        super();
    }

    public PlaybackInfoWidget(MemorySegment address) {
        super(address);
    }
}
