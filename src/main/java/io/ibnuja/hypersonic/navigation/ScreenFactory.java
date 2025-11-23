package io.ibnuja.hypersonic.navigation;

import io.ibnuja.hypersonic.ui.pages.HomePage;
import org.gnome.gtk.*;

import java.util.function.Consumer;

@SuppressWarnings("ClassCanBeRecord")
public class ScreenFactory {

    private final Consumer<Route> navigator;

    public ScreenFactory(Consumer<Route> navigator) {
        this.navigator = navigator;
    }

    public Widget create(Route route) {
        return switch (route) {
            case Route.Home _ -> new HomePage();
            case Route.NowPlaying _ -> createPlaceholder("Now Playing");
            case Route.Album _ -> createAlbumPage();
            case Route.Artists _ -> createPlaceholder("Artists");
            case Route.Songs _ -> createPlaceholder("Songs");
            case Route.AlbumRecentlyAdded _ -> createPlaceholder("Recently Added Albums");
        };
    }

    private Widget createAlbumPage() {
        Box box = new Box(Orientation.VERTICAL, 10);
        box.setHexpand(true);
        box.setVexpand(true);
        box.setHalign(Align.CENTER);
        box.setValign(Align.CENTER);

        Label label = new Label("Albums");
        label.addCssClass("title-1");
        box.append(label);

        Button button = new Button();
        button.setLabel("Go to Recently Added");
        button.addCssClass("pill");
        button.onClicked(() -> navigator.accept(new Route.AlbumRecentlyAdded()));
        box.append(button);

        return box;
    }

    private Widget createPlaceholder(String text) {
        Label label = new Label(text);
        label.setHexpand(true);
        label.setVexpand(true);
        label.setHalign(Align.CENTER);
        label.setValign(Align.CENTER);

        Box box = new Box(Orientation.VERTICAL, 0);
        box.append(label);
        return box;
    }
}
