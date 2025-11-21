package io.ibnuja.hypersonic.audio;

import lombok.extern.slf4j.Slf4j;
import org.freedesktop.gstreamer.gst.*;
import org.gnome.glib.GError;
import org.gnome.glib.GLib;
import org.javagi.base.Out;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

// Defines the functional interface required by bus.connect

@Slf4j
public class GstBackend {

    private final Element playbin;
    private final Consumer<PlaybackAction> sender;
    private final Element fakesink;

    public GstBackend(Consumer<PlaybackAction> sender) {
        this.sender = sender;
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
     * @param msgTypes
     * @param msg
     */
    private void processBusMessageOnMainThread(Set<MessageType> msgTypes, Message msg) {
        if (msgTypes.contains(MessageType.EOS)) {
            System.out.println("Player: Got Event Type: " + MessageType.EOS.name());
            GLib.print("End of stream\n");
            // Maps external calls: this.pause() and this.notifyState()
            sender.accept(new PlaybackAction.SongFinished());
            sender.accept(new PlaybackAction.Pause());
        } else if (msgTypes.contains(MessageType.ERROR)) {
            Out<GError> errorOut = new Out<>();
            Out<String> debugOut = new Out<>();
            msg.parseError(errorOut, debugOut);

            GError error = errorOut.get();
            String debug = debugOut.get();

            GLib.printerr("Error: %s\nDebug: %s\n", error != null ? error.readMessage() : "Unknown Error", debug);

            // Maps external call: loop.quit() (via Stop action on main thread)
            sender.accept(new PlaybackAction.Stop());
        } else if (msgTypes.contains(MessageType.ASYNC_DONE)) {
            // Maps external call: this.onPositionChanged()
            System.out.println("Player: Got Event Type: " + MessageType.ASYNC_DONE.name());
            sender.accept(new PlaybackAction.Seek(0));
        } else if (msgTypes.contains(MessageType.STREAM_START)) {
            // Maps external call: this.onDurationChanged() and this.onPositionChanged()
            System.out.println("Player: Got Event Type: " + MessageType.STREAM_START.name());
            sender.accept(new PlaybackAction.Seek(0));
        } else if (msgTypes.contains(MessageType.STATE_CHANGED)) {
            System.out.println("Player: playbin: Got Event Type: " + MessageType.STATE_CHANGED.name());
        } else if (msgTypes.contains(MessageType.BUFFERING)) {
            Out<Integer> percentOut = new Out<>();
            msg.parseBuffering(percentOut);
            int percent = percentOut.get();
            System.out.println("Player: Got Event Type: " + MessageType.BUFFERING.name() + ": percent=" + percent);
        } else if (msgTypes.contains(MessageType.DURATION_CHANGED)) {
            // Maps external call: this.onDurationChanged()
            System.out.println("Player: Got Event Type: " + MessageType.DURATION_CHANGED.name());
            sender.accept(new PlaybackAction.Seek(0));
        } else if (msgTypes.contains(MessageType.TOC)) {
            System.out.println("Player: Got Event Type: " + MessageType.TOC.name());
        } else if (msgTypes.contains(MessageType.TAG)) {
            System.out.println("Player: Got Event Type: " + MessageType.TAG.name());
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

    public long queryPosition() {
        Out<Long> positionOut = new Out<>();
        playbin.queryPosition(Format.TIME, positionOut);

        if (positionOut.get() != null) {
            return TimeUnit.NANOSECONDS.toSeconds(positionOut.get());
        }
        return 0;
    }

    public long queryDuration() {
        Out<Long> durationOut = new Out<>();
        playbin.queryDuration(Format.TIME, durationOut);
        if (durationOut.get() != null) {
            return TimeUnit.NANOSECONDS.toSeconds(durationOut.get());
        }
        return 0;
    }
}
