package io.ibnuja.hypersonic.navigation;

import io.ibnuja.hypersonic.ui.pages.HomePage;
import org.gnome.adw.NavigationPage;
import org.gnome.gtk.*;

import java.util.function.Consumer;

import static io.ibnuja.hypersonic.navigation.Route.*;

@SuppressWarnings("ClassCanBeRecord")
public class ScreenFactory {

    private final Consumer<Route> navigator;

    public ScreenFactory(Consumer<Route> navigator) {
        this.navigator = navigator;
    }

    public NavigationPage create(Route route) {
        if (route instanceof Route.Routes) {
            Widget content = switch (route) {
                case Routes.HOME -> new HomePage();
                case Routes.NOW_PLAYING -> createPlaceholder("Now Playing");
                case Routes.ALBUM -> createAlbumPage();
                case Routes.ARTISTS -> createPlaceholder("Artists");
                case Routes.SONGS -> createPlaceholder("Songs");
                case Routes.ALBUM_RECENTLY_ADDED -> createPlaceholder("Recently Added Albums");
            };

            // Wrap the widget in an AdwNavigationPage
            var page = new NavigationPage();
            page.setChild(content);
            page.setTag(route.id());
            page.setTitle(route.id());

            return page;
        } else {
            throw new IllegalArgumentException("Invalid route: " + route);
        }
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
        button.onClicked(() -> navigator.accept(Routes.ALBUM_RECENTLY_ADDED));
        box.append(button);

        return box;
    }

    // Example in ScreenFactory.java
    private Widget createPlaceholder(String title) {
        // We need a ToolbarView to hold the HeaderBar + Content
        var toolbarView = new org.gnome.adw.ToolbarView();

        // 1. Add the HeaderBar (Handles the Back Button automatically)
        var headerBar = new org.gnome.adw.HeaderBar();
        toolbarView.addTopBar(headerBar);

        // 2. The Content
        var box = new Box(Orientation.VERTICAL, 0);
        box.setHalign(Align.CENTER);
        box.setValign(Align.CENTER);
        box.append(new Label(title));

        toolbarView.setContent(box);

        return toolbarView;
    }
}
