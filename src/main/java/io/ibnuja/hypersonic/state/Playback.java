package io.ibnuja.hypersonic.state;

import io.ibnuja.hypersonic.model.Song;
import io.ibnuja.hypersonic.trait.UpdatableState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class Playback implements UpdatableState<Playback.Action, Playback.Event> {

    public sealed interface Action {
        record Play() implements Action {}

        record Pause() implements Action {}

        record TogglePlay() implements Action {}

        record Stop() implements Action {}

        record Next() implements Action {}

        record Previous() implements Action {}

        record SongFinished() implements Action {}

        record LoadSongs(List<Song> songs) implements Action {}

        record Queue(List<Song> songs) implements Action {}

        record Seek(long position) implements Action {}

        record SetShuffle(boolean enabled) implements Action {}

        record SetRepeatMode(RepeatMode mode) implements Action {}

        record ToggleRepeat() implements Action {}
    }

    public sealed interface Event {
        record PlaybackResumed() implements Event {}

        record PlaybackPaused() implements Event {}

        record PlaybackStopped() implements Event {}

        record TrackChanged(Song song) implements Event {}

        record TrackSeeked(long position) implements Event {}

        record PlaylistChanged() implements Event {}

        record ShuffleChanged(boolean enabled) implements Event {}

        record RepeatModeChanged(RepeatMode mode) implements Event {}
    }

    public enum RepeatMode {
        NONE, SONG, PLAYLIST;

        public RepeatMode next() {
            return values()[(this.ordinal() + 1) % values().length];
        }
    }

    private final List<Song> songs = new ArrayList<>();
    private List<Integer> playOrder = new ArrayList<>();

    private int currentPlayIndex = -1;
    private boolean isPlaying = false;
    private boolean isShuffled = false;
    private RepeatMode repeatMode = RepeatMode.NONE;

    @Override
    public List<Event> update(Action action) {
        return switch (action) {
            case Action.LoadSongs(List<Song> songs1) -> {
                this.songs.clear();
                this.songs.addAll(songs1);
                rebuildPlayOrder();
                yield playIndex(0);
            }

            case Action.Queue(List<Song> songs1) -> {
                this.songs.addAll(songs1);
                rebuildPlayOrder();
                yield List.of(new Event.PlaylistChanged());
            }

            case Action.Play _ -> {
                if (!isPlaying && currentPlayIndex != -1) {
                    this.isPlaying = true;
                    yield List.of(new Event.PlaybackResumed());
                }
                yield List.of();
            }

            case Action.Pause _ -> {
                if (isPlaying) {
                    this.isPlaying = false;
                    yield List.of(new Event.PlaybackPaused());
                }
                yield List.of();
            }

            case Action.TogglePlay _ -> {
                if (currentPlayIndex == -1) yield List.of();
                this.isPlaying = !this.isPlaying;
                yield List.of(this.isPlaying ? new Event.PlaybackResumed() : new Event.PlaybackPaused());
            }

            case Action.Stop _ -> {
                this.isPlaying = false;
                this.currentPlayIndex = -1;
                yield List.of(new Event.PlaybackStopped());
            }

            case Action.Next _ -> {
                int nextIndex = getNextIndex();
                if (nextIndex != -1) {
                    yield playIndex(nextIndex);
                } else {
                    this.isPlaying = false;
                    yield List.of(new Event.PlaybackStopped());
                }
            }

            case Action.Previous _ -> {
                int prevIndex = getPrevIndex();
                if (prevIndex != -1) {
                    yield playIndex(prevIndex);
                } else {
                    yield List.of(new Event.TrackSeeked(0));
                }
            }

            case Action.SongFinished _ -> {
                int nextIndex = getNextIndex();
                if (nextIndex != -1) {
                    yield playIndex(nextIndex);
                } else {
                    this.isPlaying = false;
                    yield List.of(new Event.PlaybackStopped());
                }
            }

            case Action.SetShuffle(var enabled) -> {
                if (this.isShuffled != enabled) {
                    this.isShuffled = enabled;

                    Song currentSong = getCurrentSong();
                    rebuildPlayOrder();
                    if (currentSong != null) {
                        this.currentPlayIndex = findIndexInPlayOrder(currentSong);
                    }

                    yield List.of(new Event.ShuffleChanged(this.isShuffled));
                }
                yield List.of();
            }

            case Action.ToggleRepeat _ -> {
                this.repeatMode = this.repeatMode.next();
                yield List.of(new Event.RepeatModeChanged(this.repeatMode));
            }

            case Action.SetRepeatMode(var mode) -> {
                this.repeatMode = mode;
                yield List.of(new Event.RepeatModeChanged(this.repeatMode));
            }

            case Action.Seek(var position) -> List.of(new Event.TrackSeeked(position));
        };
    }

    private List<Event> playIndex(int index) {
        if (index < 0 || index >= playOrder.size()) return List.of();

        this.currentPlayIndex = index;
        this.isPlaying = true;

        Song song = getCurrentSong();
        List<Event> events = new ArrayList<>();
        events.add(new Event.TrackChanged(song));
        events.add(new Event.PlaybackResumed());
        return events;
    }

    private void rebuildPlayOrder() {
        List<Integer> indices = new ArrayList<>(IntStream.range(0, songs.size()).boxed().toList());

        if (isShuffled) {
            Collections.shuffle(indices);
        }

        this.playOrder = indices;
    }

    /**
     * Logic ported from <a href="https://github.com/Diegovsky/riff/blob/dd78fd9700b5f90ab16a448a5502880bfb6255f3/src/app/state/playback_state.rs#L198">next_index</a>
     */
    private int getNextIndex() {
        if (playOrder.isEmpty()) return -1;

        if (repeatMode == RepeatMode.SONG && currentPlayIndex != -1) {
            return currentPlayIndex;
        }

        int next = currentPlayIndex + 1;
        if (next >= playOrder.size()) {
            if (repeatMode == RepeatMode.PLAYLIST) {
                return 0;
            }
            return -1;
        }
        return next;
    }

    /**
     * Logic ported <a href="https://github.com/Diegovsky/riff/blob/dd78fd9700b5f90ab16a448a5502880bfb6255f3/src/app/state/playback_state.rs#L223">prev_index</a>
     */
    private int getPrevIndex() {
        if (playOrder.isEmpty()) return -1;

        if (repeatMode == RepeatMode.SONG && currentPlayIndex != -1) {
            return currentPlayIndex;
        }

        int prev = currentPlayIndex - 1;
        if (prev < 0) {
            if (repeatMode == RepeatMode.PLAYLIST) {
                return playOrder.size() - 1;
            }
            return -1;
        }
        return prev;
    }

    private Song getCurrentSong() {
        if (currentPlayIndex >= 0 && currentPlayIndex < playOrder.size()) {
            int actualIndex = playOrder.get(currentPlayIndex);
            return songs.get(actualIndex);
        }
        return null;
    }

    private int findIndexInPlayOrder(Song song) {
        int actualIndex = songs.indexOf(song);
        return playOrder.indexOf(actualIndex);
    }
}
