package io.ibnuja.hypersonic.ui.components.playback;

import io.ibnuja.hypersonic.Hypersonic;
import io.ibnuja.hypersonic.state.ConnectionState;
import io.ibnuja.hypersonic.state.Playback;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.gnome.gdk.Texture;
import org.gnome.glib.GLib;
import org.gnome.gtk.Button;
import org.gnome.gtk.Image;
import org.gnome.gtk.Label;
import org.javagi.base.GErrorException;
import org.javagi.gobject.annotations.InstanceInit;
import org.javagi.gtk.annotations.GtkChild;
import org.javagi.gtk.annotations.GtkTemplate;

import java.io.IOException;
import java.lang.foreign.MemorySegment;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.CompletableFuture;

@Slf4j
@SuppressWarnings("java:S110")
@EqualsAndHashCode(callSuper = true)
@GtkTemplate(name = "PlaybackInfoWidget", ui = "/io/ibnuja/Hypersonic/components/playback/playback_info.ui")
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
        log.debug("PlaybackInfoWidget initialized");  // Add this
        Hypersonic.audioPlayer.subscribe(this::onEvent);
    }

    private void onEvent(Playback.Event event) {
        switch (event) {
            case Playback.Event.TrackChanged(var song) -> {
                String coverArtUrl = ConnectionState.INSTANCE.getApi().getCoverArtUrl(song.getCoverArt(), 60, true);
                if (coverArtUrl != null) {
                    log.debug("Loading cover art from {}", coverArtUrl);
                    loadImageFromUrl(coverArtUrl);
                }
                currentSongInfo.setVisible(true);
                String markup = "<b>" + escape(song.getTitle()) + "</b>\n" + escape(song.getArtist());
                currentSongInfo.setMarkup(markup);
            }
            case Playback.Event.PlaybackStopped _ -> currentSongInfo.setLabel("No song playing");
            default -> log.warn("Unhandled event: {}. Do nothing", event);
        }
    }

    private void loadImageFromUrl(String url) {
        CompletableFuture.runAsync(() -> {
            try {
                byte[] imageData = downloadImage(url);
                GLib.idleAdd(GLib.PRIORITY_DEFAULT_IDLE, () -> {
                    //FIXME use Libglycin
                    try {
                        var texture = Texture.fromBytes(imageData);
                        playingImage.setFromPaintable(texture);
                        playingImage.setPixelSize(60);
                    } catch (GErrorException e) {
                        throw new RuntimeException(e);
                    }

                    return false;
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Interrupted while loading cover art", e);
            } catch (Exception e) {
                log.error("Failed to load cover art", e);
            }
        });
    }

    private byte[] downloadImage(String url) throws InterruptedException, IOException {
        try (var client = HttpClient.newHttpClient()) {
            HttpResponse<byte[]> response;
            var request = HttpRequest.newBuilder()
                    .uri(java.net.URI.create(url))
                    .build();
            response = client.send(request, BodyHandlers.ofByteArray());
            return response.body();
        }
    }

    private String escape(String text) {
        return text == null ? "" : text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
