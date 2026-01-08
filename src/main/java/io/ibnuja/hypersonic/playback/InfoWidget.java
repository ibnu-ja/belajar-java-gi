package io.ibnuja.hypersonic.playback;

import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.gnome.gtk.Button;
import org.gnome.gtk.Image;
import org.gnome.gtk.Label;
import org.javagi.gobject.annotations.InstanceInit;
import org.javagi.gtk.annotations.GtkChild;
import org.javagi.gtk.annotations.GtkTemplate;

import java.lang.foreign.MemorySegment;

@Slf4j
@SuppressWarnings("java:S110")
@EqualsAndHashCode(callSuper = true)
@GtkTemplate(name = "PlaybackInfoWidget", ui = "/io/ibnuja/Hypersonic/components/playback/playback_info.ui")
public class InfoWidget extends Button {

    @GtkChild(name = "current_song_info")
    public Label currentSongInfo;

    @GtkChild(name = "playing_image")
    public Image playingImage;

    @SuppressWarnings("unused")
    public InfoWidget() {
        super();
    }

    @SuppressWarnings("unused")
    public InfoWidget(MemorySegment address) {
        super(address);
    }

    @InstanceInit
    @SuppressWarnings("unused")
    public void init() {
        log.trace("PlaybackInfoWidget initialized");
    }
}
