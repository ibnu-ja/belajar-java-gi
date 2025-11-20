package io.ibnuja.hypersonic.state;

import io.ibnuja.hypersonic.model.Song;
import org.gnome.gobject.GObject;
import org.javagi.gobject.annotations.Property;
import org.javagi.gobject.annotations.RegisteredType;
import org.javagi.gobject.types.Types;

@RegisteredType(name = "HypersonicPlayerState")
public class PlayerState extends GObject {

    static {
        Types.register(PlayerState.class);
    }

    private boolean isPlaying = false;
    private Song currentSong;
    private long position = 0;
    private double volume = 1.0;

    public PlayerState() {
        super();
    }

    @Property(name = "playing")
    public boolean isPlaying() { return isPlaying; }

    @Property(name = "playing")
    public void setPlaying(boolean playing) {
        this.isPlaying = playing;
    }

    @Property(name = "song")
    public Song getCurrentSong() { return currentSong; }

    @Property(name = "song")
    public void setCurrentSong(Song song) {
        this.currentSong = song;
        //reset
        setPosition(0);
    }

    @Property(name = "position")
    public long getPosition() { return position; }

    @Property(name = "position")
    public void setPosition(long position) {
        this.position = position;
    }

    @Property(name = "volume")
    public double getVolume() { return volume; }

    @Property(name = "volume")
    public void setVolume(double volume) {
        this.volume = volume;
    }
}
