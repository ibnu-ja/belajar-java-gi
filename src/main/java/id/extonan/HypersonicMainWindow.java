package id.extonan;

import io.github.jwharm.javagi.base.GErrorException;
import io.github.jwharm.javagi.base.Out;
import io.github.jwharm.javagi.gobject.annotations.InstanceInit;
import io.github.jwharm.javagi.gtk.annotations.GtkChild;
import io.github.jwharm.javagi.gtk.annotations.GtkTemplate;
import lombok.EqualsAndHashCode;
import org.gnome.gio.File;
import org.gnome.gio.MenuModel;
import org.gnome.gio.SettingsBindFlags;
import org.gnome.gtk.*;
import org.gnome.gio.Settings;

@GtkTemplate(ui="/id/extonan/hypersonic/window.ui")
@SuppressWarnings({"java:S110", "java:S112"})
@EqualsAndHashCode(callSuper = true)
public class HypersonicMainWindow extends ApplicationWindow {

    @GtkChild
    public Stack fileStacks;

    @GtkChild
    public MenuButton hamburger;

    protected Settings settings;

    public HypersonicMainWindow(HypersonicApp app) {
        setApplication(app);
    }

    @InstanceInit
    @SuppressWarnings("unused")
    public void init() {
        var builder = GtkBuilder.fromResource("/id/extonan/hypersonic/menu.ui");
        var menu = (MenuModel) builder.getObject("settings");
        hamburger.setMenuModel(menu);

        settings = new Settings("id.extonan.hypersonic");
        settings.bind("transition", fileStacks, "transition-type", SettingsBindFlags.DEFAULT);
    }

    public void open(File file) {
        String basename = file.getBasename();

        var view = new TextView();
        view.setEditable(false);
        view.setCursorVisible(false);

        var scrolled = new ScrolledWindow();
        scrolled.setHexpand(true);
        scrolled.setVexpand(true);
        scrolled.setChild(view);
        fileStacks.addTitled(scrolled, basename, basename);
        var buffer = view.getBuffer();
        try {
            var contents = new Out<byte[]>();
            if (file.loadContents(null, contents, null)) {
                String str = new String(contents.get());
                buffer.setText(str, str.length());
            }
        } catch (GErrorException e) {
            throw new RuntimeException(e);
        }

        var tag = buffer.createTag(null, null);
        settings.bind("font", tag, "font", SettingsBindFlags.DEFAULT);
        TextIter startIter = new TextIter();
        TextIter endIter = new TextIter();
        buffer.getStartIter(startIter);
        buffer.getEndIter(endIter);
        buffer.applyTag(tag, startIter, endIter);
    }
}
