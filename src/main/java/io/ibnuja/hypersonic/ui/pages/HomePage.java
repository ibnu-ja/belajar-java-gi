package io.ibnuja.hypersonic.ui.pages;

import lombok.EqualsAndHashCode;
import org.gnome.adw.NavigationPage;
import org.gnome.gtk.Label;
import org.javagi.gtk.annotations.GtkTemplate;

import static org.javagi.util.Intl.i18n;

@GtkTemplate(name = "HomePage", ui = "/io/ibnuja/Hypersonic/pages/home.ui")
@EqualsAndHashCode(callSuper = true)
@SuppressWarnings("java:S110")
public class HomePage extends NavigationPage {

    public HomePage() {
        setTitle(i18n("Home"));
        setTag("home");

        var label = new Label(i18n("Welcome to Hypersonic!"));
        label.addCssClass("title-1");
        setChild(label);
    }
}
