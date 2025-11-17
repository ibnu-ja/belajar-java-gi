package io.ibnuja;

import io.github.jwharm.javagi.gtk.annotations.GtkTemplate;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.gnome.adw.NavigationPage;

@Slf4j
@GtkTemplate(ui = "/io/ibnuja/hypersonic/sidebar.ui")
@SuppressWarnings({"java:S110", "java:S1192"})
@EqualsAndHashCode(callSuper = true)
public class HypersonicSidebar extends NavigationPage {

    public HypersonicSidebar() {
        super();
    }

}