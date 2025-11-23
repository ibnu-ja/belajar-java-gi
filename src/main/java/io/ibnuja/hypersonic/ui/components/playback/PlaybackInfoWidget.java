package io.ibnuja.hypersonic.ui.components.playback;

import io.ibnuja.hypersonic.Hypersonic;
import io.ibnuja.hypersonic.state.Playback;
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
@GtkTemplate(name = "PlaybackInfoWidget", ui = "/io/ibnuja/hypersonic/components/playback/playback_info.ui")
public class PlaybackInfoWidget extends Button {

    @GtkChild(name = "current_song_info")
    public Label currentSongInfo;

    @GtkChild(name = "playing_image")
    public Image playingImage;

    public PlaybackInfoWidget() {
        super();
    }

    public PlaybackInfoWidget(MemorySegment address) {
        super(address);
    }

    @InstanceInit
    @SuppressWarnings("unused")
    public void init() {
        Hypersonic.audioPlayer.subscribe(this::onEvent);
    }

    private void onEvent(Playback.Event event) {
        switch (event) {
            case Playback.Event.TrackChanged(var song) -> {
                currentSongInfo.setVisible(true);
                String markup = "<b>" + escape(song.title()) + "</b>\n" + escape(song.artist());
                currentSongInfo.setMarkup(markup);
            }
            case Playback.Event.PlaybackStopped _ -> currentSongInfo.setLabel("No song playing");
            default -> log.warn("Unhandled event: {}. Do nothing", event);
        }
    }

    private String escape(String text) {
        return text == null ? "" : text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
