package io.ibnuja.hypersonic.navigation;

public sealed  interface Route permits Route.Home {

    String name();

    record Home() implements Route {
        @Override
        public String name() {
            return "home";
        }
    }
}
