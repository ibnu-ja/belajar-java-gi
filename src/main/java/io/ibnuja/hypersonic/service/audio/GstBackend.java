package io.ibnuja.hypersonic.service.audio;

import lombok.extern.slf4j.Slf4j;
import org.freedesktop.gstreamer.gst.*;
import org.gnome.glib.GError;
import org.gnome.glib.GLib;
import org.javagi.base.Out;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
public class GstBackend implements Backend {

    private final Element playbin;
    private final Element fakesink;

    public GstBackend() {
        playbin = ElementFactory.make("playbin", "audio-player");
        if (playbin == null) {
            throw new IllegalStateException("Failed to create playbin element");
        }

        fakesink = ElementFactory.make("fakesink", "video-fakesink");
        if (fakesink != null) {
            playbin.set("video-sink", fakesink);
        }

        setupBus();
    }

    /**
     * <a href="https://gitlab.com/esiqveland/subsound-gtk/-/blob/217ba95ef9e8a39a99ff0126d09bf7f68a47928c/src/main/java/com/github/subsound/sound/PlaybinPlayer.java">sorse</a> <br>
     * See: <a href="https://java-gi.org/javadoc/org/freedesktop/gstreamer/gst/Bus.html">Bus</a>
     */
    private void setupBus() {
        Bus bus = playbin.getBus();
        if (bus == null) {
            log.error("Failed to get bus from playbin");
            return;
        }

        bus.addSignalWatch();

        bus.connect(
                "message", (Bus.MessageCallback) (Message msg) -> {
                    if (msg == null) return;

                    // Retrieve the message type synchronously
                    Set<MessageType> msgTypes = msg.readType();
                    if (msgTypes == null) return;

                    if (msgTypes.contains(MessageType.EOS)) {
                        log.debug("EOS received");
                        GLib.idleAdd(
                                GLib.PRIORITY_DEFAULT_IDLE, () -> {
//                                    dispatcher.accept(new Playback.Action.SongFinished());
                                    return false;
                                }
                        );
                    } else if (msgTypes.contains(MessageType.ERROR)) {
                        Out<GError> errorOut = new Out<>();
                        Out<String> debugOut = new Out<>();
                        msg.parseError(errorOut, debugOut);
                        String errorMsg = errorOut.get() != null ? errorOut.get().readMessage() : "Unknown Error";
                        String debugMsg = debugOut.get();
                        log.error("GStreamer Error: {} - Debug: {}", errorMsg, debugMsg);
                        GLib.idleAdd(
                                GLib.PRIORITY_DEFAULT_IDLE, () -> {
//                                    dispatcher.accept(new Playback.Action.Stop());
                                    return false;
                                }
                        );
                    }
                }
        );
    }

    public void setUri(String uri) {
        playbin.setState(State.READY);
        playbin.set("uri", uri);
    }

    public void play() {
        playbin.setState(State.PLAYING);
    }

    public void pause() {
        playbin.setState(State.PAUSED);
    }

    public void stop() {
        playbin.setState(State.NULL);
    }

    @SuppressWarnings("unused")
    public long queryPosition() {
        Out<Long> positionOut = new Out<>();
        if (playbin.queryPosition(Format.TIME, positionOut)) {
            Long pos = positionOut.get();
            return pos != null ? TimeUnit.NANOSECONDS.toSeconds(pos) : 0;
        }
        return 0;
    }

    @SuppressWarnings("unused")
    public long queryDuration() {
        Out<Long> durationOut = new Out<>();
        if (playbin.queryDuration(Format.TIME, durationOut)) {
            Long dur = durationOut.get();
            return dur != null ? TimeUnit.NANOSECONDS.toSeconds(dur) : 0;
        }
        return 0;
    }
}
