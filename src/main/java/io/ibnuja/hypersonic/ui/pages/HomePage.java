package io.ibnuja.hypersonic.ui.pages;

import lombok.EqualsAndHashCode;
import org.gnome.gtk.Box;
import org.javagi.gtk.annotations.GtkTemplate;

import java.lang.foreign.MemorySegment;

@GtkTemplate(name = "HomePage", ui = "/io/ibnuja/Hypersonic/pages/home.ui")
@EqualsAndHashCode(callSuper = true)
@SuppressWarnings("java:S110")
public class HomePage extends Box {

    public HomePage() {
        super();
    }

    public HomePage(MemorySegment address) {
        super(address);
    }
}
