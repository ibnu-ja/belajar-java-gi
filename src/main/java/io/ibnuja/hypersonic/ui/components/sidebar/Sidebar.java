package io.ibnuja.hypersonic.ui.components.sidebar;

import org.javagi.gtk.annotations.GtkTemplate;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.gnome.adw.NavigationPage;

@Slf4j
@GtkTemplate(ui = "/io/ibnuja/hypersonic/components/sidebar/sidebar.ui", name = "Sidebar")
@SuppressWarnings({"java:S110", "java:S1192", "unused"})
@EqualsAndHashCode(callSuper = true)
public class Sidebar extends NavigationPage {

    public Sidebar() {
        super();
    }

}
