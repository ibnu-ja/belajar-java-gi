package io.ibnuja.hypersonic.ui.components.selection;

import io.github.jwharm.javagi.gtk.annotations.GtkTemplate;
import org.gnome.gtk.Box;

import java.lang.foreign.MemorySegment;

@GtkTemplate(ui = "/io/ibnuja/hypersonic/components/selection/selection_toolbar.ui", name = "SelectionToolbarWidget")
public class SelectionToolbar extends Box {

    public SelectionToolbar() {
        super();
    }

    public SelectionToolbar(MemorySegment address) {
        super(address);
    }
}
