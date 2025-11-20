package io.ibnuja.hypersonic.audio;

import io.ibnuja.hypersonic.model.Song;

public interface PlaybackAction {

    record Play() implements PlaybackAction {}

    record Pause() implements PlaybackAction {}

    record Stop() implements PlaybackAction {}

    record LoadSong(Song song) implements PlaybackAction {}

    record Seek(double position) implements PlaybackAction {}

    record SongFinished() implements PlaybackAction {}

    record PositionChanged(long position) implements PlaybackAction {} // Seconds
}
