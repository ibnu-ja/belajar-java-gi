package io.ibnuja.hypersonic.ui.components.sidebar;

import lombok.EqualsAndHashCode;
import org.gnome.gtk.Image;
import org.gnome.gtk.Label;
import org.gnome.gtk.ListBoxRow;
import org.javagi.gtk.annotations.GtkChild;
import org.javagi.gtk.annotations.GtkTemplate;

import java.lang.foreign.MemorySegment;

@GtkTemplate(name = "SidebarRow", ui = "/io/ibnuja/hypersonic/components/sidebar/sidebar_row.ui")
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

    public void setItem(SidebarItem item) {
        title.setLabel(item.title());
        if (item.iconName() != null) {
            icon.setFromIconName(item.iconName());
        } else {
            icon.setVisible(false);
        }
        setTooltipText(item.title());
    }
}
