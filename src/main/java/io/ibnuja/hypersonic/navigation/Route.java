package io.ibnuja.hypersonic.navigation;

public sealed interface Route permits Route.Home, Route.Album, Route.Artists, Route.Songs, Route.NowPlaying, Route.AlbumRecentlyAdded {

    String name();

    record Home() implements Route {
        @Override
        public String name() { return "home"; }
    }

    record NowPlaying() implements Route {
        @Override
        public String name() { return "now_playing"; }
    }

    record Album() implements Route {
        @Override
        public String name() { return "album"; }
    }

    record Artists() implements Route {
        @Override
        public String name() { return "artists"; }
    }

    record Songs() implements Route {
        @Override
        public String name() { return "songs"; }
    }

    record AlbumRecentlyAdded() implements Route {
        @Override
        public String name() { return "album_recently_added"; }
    }
}
