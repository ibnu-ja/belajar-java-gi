package io.ibnuja.hypersonic.model;

import org.gnome.gobject.GObject;
import org.javagi.gobject.annotations.Property;
import org.javagi.gobject.annotations.RegisteredType;
import org.javagi.gobject.types.Types;

@RegisteredType(name = "HypersonicSong")
public class Song extends GObject {

    static {
        Types.register(Song.class);
    }

    private String id;
    private String title;
    private String artist;
    private String uri;
    private long duration; // in seconds

    public Song() {
        super();
    }

    public Song(String id, String title, String artist, String uri, long duration) {
        super();
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.uri = uri;
        this.duration = duration;
    }

    @Property(name = "title")
    public String getTitle() { return title; }

    @Property(name = "title")
    public void setTitle(String title) { this.title = title; }

    @Property(name = "artist")
    public String getArtist() { return artist; }

    @Property(name = "artist")
    public void setArtist(String artist) { this.artist = artist; }

    @Property(name = "uri")
    public String getUri() { return uri; }

    @Property(name = "duration")
    public long getDuration() { return duration; }

    @Property(name = "id")
    public String getId() { return id; }
}
