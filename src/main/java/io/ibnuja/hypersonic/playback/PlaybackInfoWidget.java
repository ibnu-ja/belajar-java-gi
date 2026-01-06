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
    }
//    private void loadImageFromUrl(String url) {
//        CompletableFuture.runAsync(() -> {
//            try {
//                byte[] imageData = downloadImage(url);
//                GLib.idleAdd(GLib.PRIORITY_DEFAULT_IDLE, () -> {
//                    //FIXME use Libglycin
//                    try {
//                        var texture = Texture.fromBytes(imageData);
//                        playingImage.setFromPaintable(texture);
//                        playingImage.setPixelSize(60);
//                    } catch (GErrorException e) {
//                        throw new RuntimeException(e);
//                    }
//
//                    return false;
//                });
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                log.error("Interrupted while loading cover art", e);
//            } catch (Exception e) {
//                log.error("Failed to load cover art", e);
//            }
//        });
//    }
//
//    private byte[] downloadImage(String url) throws InterruptedException, IOException {
//        try (var client = HttpClient.newHttpClient()) {
//            HttpResponse<byte[]> response;
//            var request = HttpRequest.newBuilder()
//                    .uri(java.net.URI.create(url))
//                    .build();
//            response = client.send(request, BodyHandlers.ofByteArray());
//            return response.body();
//        }
//    }
//
//    private String escape(String text) {
//        return text == null ? "" : text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
//    }
}
