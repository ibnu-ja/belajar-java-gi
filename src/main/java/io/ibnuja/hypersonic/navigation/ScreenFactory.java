package io.ibnuja.hypersonic.navigation;

import io.ibnuja.hypersonic.ui.pages.HomePage;
import org.gnome.gtk.Widget;

public class ScreenFactory {

    public Widget create(Route route) {
        return switch (route) {
            case Route.Home _ -> new HomePage();
        };
    }
}
