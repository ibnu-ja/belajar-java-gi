package io.ibnuja.hypersonic.navigation;

public sealed interface Route permits Route.Home, Route.Album, Route.Artists, Route.Songs, Route.NowPlaying, Route.AlbumRecentlyAdded {

    String name();

    String id();

    record Home() implements Route {
        @Override
        public String name() {
            return "Home";
        }

        @Override
        public String id() {
            return "home";
        }
    }

    record NowPlaying() implements Route {
        @Override
        public String name() {
            return "Now Playing";
        }

        @Override
        public String id() {
            return "now_playing";
        }
    }

    record Album() implements Route {
        @Override
        public String id() {
            return "album";
        }

        @Override
        public String name() {
            return "Album";
        }
    }

    record Artists() implements Route {
        @Override
        public String name() {
            return "Artist";
        }

        @Override
        public String id() {
            return "artists";
        }
    }

    record Songs() implements Route {
        @Override
        public String name() {
            return "Songs";
        }

        @Override
        public String id() {
            return "songs";
        }
    }

    record AlbumRecentlyAdded() implements Route {
        @Override
        public String name() {
            return "Recently Added Album";
        }

        @Override
        public String id() {
            return "album_recently_added";
        }
    }
}
