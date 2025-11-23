package io.ibnuja.hypersonic.audio;

import io.ibnuja.hypersonic.state.Playback;
import lombok.extern.slf4j.Slf4j;
import org.freedesktop.gstreamer.gst.*;
import org.gnome.glib.GError;
import org.gnome.glib.GLib;
import org.javagi.base.Out;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
public class GstBackend {

    private final Element playbin;
    private final Consumer<Playback.Action> dispatcher;
    @SuppressWarnings("FieldCanBeLocal")
    private final Element fakesink;

    public GstBackend(Consumer<Playback.Action> dispatcher) {
        this.dispatcher = dispatcher;
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

    private void setupBus() {
        Bus bus = playbin.getBus();
        if (bus == null) {
            log.error("Failed to get bus from playbin");
            return;
        }

        bus.connect(
                "message", (Bus.MessageCallback) (Message msg) -> {
                    Set<MessageType> msgTypes = msg.readType();
                    GLib.idleAdd(
                            GLib.PRIORITY_DEFAULT_IDLE, () -> {
                                GstBackend.this.processBusMessageOnMainThread(msgTypes, msg);
                                return false;
                            }
                    );
                }
        );

        bus.addSignalWatch();
    }

    /**
     * <a href="https://gitlab.com/esiqveland/subsound-gtk/-/blob/217ba95ef9e8a39a99ff0126d09bf7f68a47928c/src/main/java/com/github/subsound/sound/PlaybinPlayer.java">sorse</a>
     */
    @SuppressWarnings("StatementWithEmptyBody")
    private void processBusMessageOnMainThread(Set<MessageType> msgTypes, Message msg) {
        if (msgTypes.contains(MessageType.ERROR)) {
            Out<GError> errorOut = new Out<>();
            Out<String> debugOut = new Out<>();
            msg.parseError(errorOut, debugOut);

            GError error = errorOut.get();
            String debug = debugOut.get();

            GLib.printerr("Error: %s\nDebug: %s\n", error != null ? error.readMessage() : "Unknown Error", debug);
            dispatcher.accept(new Playback.Action.Stop());
        } else if (msgTypes.contains(MessageType.EOS)) {
            dispatcher.accept(new Playback.Action.SongFinished());
        } else if (msgTypes.containsAll(List.of(
                MessageType.ASYNC_DONE,
                MessageType.STREAM_START,
                MessageType.STATE_CHANGED,
                MessageType.DURATION_CHANGED
        ))) {
            dispatcher.accept(new Playback.Action.Seek(0));
        } else if (msgTypes.contains(MessageType.BUFFERING)) {
            Out<Integer> percentOut = new Out<>();
            msg.parseBuffering(percentOut);
            int percent = percentOut.get();
            log.info("Player: Got Event Type: {}, percent: {}", MessageType.BUFFERING.name(), percent);
        } else if (msgTypes.contains(MessageType.TOC)) {
            //TODO handle event if needed
        } else if (msgTypes.contains(MessageType.TAG)) {
            //TODO handle event if needed
        }
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
        playbin.queryPosition(Format.TIME, positionOut);

        if (positionOut.get() != null) {
            return TimeUnit.NANOSECONDS.toSeconds(positionOut.get());
        }
        return 0;
    }

    @SuppressWarnings("unused")
    public long queryDuration() {
        Out<Long> durationOut = new Out<>();
        playbin.queryDuration(Format.TIME, durationOut);
        if (durationOut.get() != null) {
            return TimeUnit.NANOSECONDS.toSeconds(durationOut.get());
        }
        return 0;
    }
}
