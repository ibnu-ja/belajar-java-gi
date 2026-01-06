package io.ibnuja.hypersonic.navigation.sidebar;

import lombok.EqualsAndHashCode;
import org.gnome.gtk.Image;
import org.gnome.gtk.Label;
import org.gnome.gtk.ListBoxRow;
import org.javagi.gtk.annotations.GtkChild;
import org.javagi.gtk.annotations.GtkTemplate;

import java.lang.foreign.MemorySegment;

@GtkTemplate(name = "SidebarRow", ui = "/io/ibnuja/Hypersonic/components/sidebar/sidebar_row.ui")
@EqualsAndHashCode(callSuper = true)
@SuppressWarnings("java:S110")
public class SidebarRow extends ListBoxRow {

    @GtkChild
    public Image icon;

    @GtkChild
    public Label title;

    public SidebarRow() {
        super();
    }

    public SidebarRow(MemorySegment address) {
        super(address);
    }
}
