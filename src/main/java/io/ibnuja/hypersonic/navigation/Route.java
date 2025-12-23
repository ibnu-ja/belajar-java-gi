// src/main/java/io/ibnuja/hypersonic/navigation/Route.java
package io.ibnuja.hypersonic.navigation;

import io.ibnuja.hypersonic.Hypersonic;
import io.ibnuja.hypersonic.ui.pages.HomePage;
import io.ibnuja.hypersonic.ui.pages.PlaceholderPage;
import org.gnome.adw.NavigationPage;
import org.gnome.gtk.*;

public sealed interface Route permits Route.Routes {

    String id();

    String title();

    NavigationPage page();

    @SuppressWarnings("java:S1192")
    enum Routes implements Route {
        HOME("home", "Home"),
        NOW_PLAYING("now_playing", "Now Playing"),
        ALBUM("album", "Albums"),
        ARTISTS("artists", "Artists"),
        SONGS("songs", "Songs"),
        ALBUM_RECENTLY_ADDED("album_recently_added", "Recent Albums");

        private final String id;
        private final String title;

        Routes(String id, String title) {
            this.id = id;
            this.title = title;
        }

        @Override
        public String id() {
            return id;
        }

        @Override
        public String title() {
            return title;
        }

        @Override
        public NavigationPage page() {
            // "Zero Boilerplate": The enum knows how to create its own page.
            return switch (this) {
                // Ensure HomePage extends NavigationPage (see Step 2)
                case HOME -> new HomePage();

                case ALBUM -> {
                    var page = new NavigationPage();
                    page.setTitle("Albums");
                    page.setTag(ALBUM.id());

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
                    button.onClicked(() -> Hypersonic.navigate(Routes.ALBUM_RECENTLY_ADDED));
                    box.append(button);

                    page.setChild(box);


                    yield page;
                }

                // Helper for pages you haven't built yet
                default -> new PlaceholderPage(this);
            };
        }
    }
}
