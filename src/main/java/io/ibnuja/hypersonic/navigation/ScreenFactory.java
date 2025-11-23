package io.ibnuja.hypersonic.navigation;

import io.ibnuja.hypersonic.ui.pages.HomePage;
import org.gnome.gtk.Box;
import org.gnome.gtk.Label;
import org.gnome.gtk.Widget;

public class ScreenFactory {

    public Widget create(Route route) {
        return switch (route) {
            case Route.Home _ -> new HomePage();
            case Route.NowPlaying _ -> createPlaceholder("Now Playing");
            case Route.Album _ -> createPlaceholder("Albums");
            case Route.Artists _ -> createPlaceholder("Artists");
            case Route.Songs _ -> createPlaceholder("Songs");
        };
    }

    private Widget createPlaceholder(String text) {
        Label label = new Label(text);
        label.setHexpand(true);
        label.setVexpand(true);
        label.setHalign(org.gnome.gtk.Align.CENTER);
        label.setValign(org.gnome.gtk.Align.CENTER);

        Box box = new Box(org.gnome.gtk.Orientation.VERTICAL, 0);
        box.append(label);
        return box;
    }
}
