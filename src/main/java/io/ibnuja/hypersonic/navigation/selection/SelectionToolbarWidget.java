package io.ibnuja.hypersonic.navigation.selection;

import lombok.EqualsAndHashCode;
import org.gnome.gtk.ActionBar;
import org.gnome.gtk.Button;
import org.gnome.gtk.MenuButton;
import org.javagi.gtk.annotations.GtkChild;
import org.javagi.gtk.annotations.GtkTemplate;
import org.gnome.gtk.Box;

import java.lang.foreign.MemorySegment;

@SuppressWarnings("java:S110")
@EqualsAndHashCode(callSuper = true)
@GtkTemplate(name = "SelectionToolbarWidget", ui = "/io/ibnuja/Hypersonic/components/selection/selection_toolbar.ui")
public class SelectionToolbarWidget extends Box {

    @GtkChild(name = "action_bar")
    public ActionBar actionBar;

    @GtkChild(name = "move_up")
    public Button moveUp;

    @GtkChild(name = "move_down")
    public Button moveDown;

    @GtkChild(name = "add")
    public MenuButton addMenu;

    @GtkChild(name = "remove")
    public Button removeButton;

    @GtkChild(name = "queue")
    public Button queueButton;

    @GtkChild(name = "save")
    public Button saveButton;

    public SelectionToolbarWidget() {
        super();
    }

    public SelectionToolbarWidget(MemorySegment address) {
        super(address);
    }
}
