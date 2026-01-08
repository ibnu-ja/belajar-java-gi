package io.ibnuja.hypersonic.playback;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.gnome.gobject.GObject;
import org.javagi.gobject.annotations.RegisteredType;

@Slf4j
@RegisteredType(name = "PlayerState")
@EqualsAndHashCode(callSuper = true)
public class PlayerState extends GObject {

    @Getter
    private boolean playing;

    public void setPlaying(boolean playing) {
        log.debug("setPlaying {}", playing);
        this.playing = playing;
        notify("playing");
    }

    public void togglePlay() {
        setPlaying(!playing);
    }
}
