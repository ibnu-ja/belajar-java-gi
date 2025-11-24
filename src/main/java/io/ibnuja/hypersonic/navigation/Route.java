package io.ibnuja.hypersonic.navigation;

public sealed interface Route permits Route.Routes {

    String id();

    enum Routes implements Route {
        HOME("home"),
        NOW_PLAYING("now_playing"),
        ALBUM("album"),
        ARTISTS("artists"),
        SONGS("songs"),
        ALBUM_RECENTLY_ADDED("album_recently_added");

        private final String id;

        Routes(String id) {
            this.id = id;
        }

        public String id() {
            return id;
        }
    }
}
