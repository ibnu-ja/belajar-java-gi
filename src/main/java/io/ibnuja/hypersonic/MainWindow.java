package io.ibnuja.hypersonic;

import io.ibnuja.hypersonic.navigation.HomePage;
import io.ibnuja.hypersonic.playback.PlaybackWidget;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.gnome.adw.ApplicationWindow;
import org.gnome.adw.NavigationSplitView;
import org.gnome.adw.NavigationView;
import org.gnome.gio.Settings;
import org.gnome.gtk.ListBox;
import org.javagi.gobject.annotations.InstanceInit;
import org.javagi.gtk.annotations.GtkChild;
import org.javagi.gtk.annotations.GtkTemplate;

@GtkTemplate(ui = "/io/ibnuja/Hypersonic/window.ui", name = "MainWindow")
@SuppressWarnings({"java:S110", "java:S112", "java:S125"})
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class MainWindow extends ApplicationWindow {

    @GtkChild(name = "navigation_view")
    public NavigationView navigationView;

    @GtkChild(name = "split_view")
    public NavigationSplitView splitView;

    @GtkChild(name = "home_listbox")
    public ListBox homeListBox;

    protected Settings settings;

    @GtkChild(name = "playback")
    public PlaybackWidget playbackWidget;

    public MainWindow(Hypersonic.Application app) {
        log.trace("MainWindow constructor");
        setApplication(app);
        playbackWidget.setup(app.playerState);
    }

    @InstanceInit
    @SuppressWarnings("unused")
    public void init() {
        log.trace("MainWindow init");
        settings = new Settings("io.ibnuja.Hypersonic");

        navigationView.push(new HomePage());
    }

}
