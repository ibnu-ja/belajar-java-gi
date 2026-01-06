package io.ibnuja.hypersonic.playback;

import io.ibnuja.hypersonic.model.Song;
import lombok.Getter;
import lombok.Setter;
import org.gnome.gobject.GObject;
import org.javagi.gio.ListModelJavaList;
import org.javagi.gobject.annotations.RegisteredType;

import java.util.ArrayList;
import java.util.List;

@RegisteredType(name = "PlaybackViewModel")
public class PlaybackViewModel extends GObject {

    @Getter @Setter
    private List<Song> queue;

    private PlaybackViewModel( ) {
        super();
        this.queue = new ArrayList<>();
    }



}
