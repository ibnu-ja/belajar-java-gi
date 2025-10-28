package id.extonan;

import io.github.jwharm.javagi.gobject.annotations.InstanceInit;
import io.github.jwharm.javagi.gtk.annotations.GtkChild;
import io.github.jwharm.javagi.gtk.annotations.GtkTemplate;

import lombok.EqualsAndHashCode;
import org.gnome.gio.Settings;
import org.gnome.gobject.ParamSpec;
import org.gnome.gtk.*;
import org.gnome.pango.FontDescription;
import org.gnome.gobject.GObject;

// Import List and Arrays for the string mapping
import java.util.List;
import java.util.Arrays;

@GtkTemplate(ui = "/id/extonan/hypersonic/settings.ui")
@SuppressWarnings({"java:S110", "java:S1192"})
@EqualsAndHashCode(callSuper = true)
public class HypersonicSettings extends Window {

    @GtkChild
    public FontDialogButton font;

    @GtkChild
    public DropDown transition;

    Settings settings;

    private static final List<String> TRANSITION_IDS = Arrays.asList(
            "none",
            "crossfade",
            "slide-left-right"
    );

    @InstanceInit
    public void init() {
        settings = new Settings("id.extonan.hypersonic");
        var fontString = settings.getString("font");
        var fontDesc = FontDescription.fromString(fontString);
        font.setFontDesc(fontDesc);
        GObject.NotifyCallback aaa = (ParamSpec paramSpec) -> {
            if (!paramSpec.getName().equals("font-desc")) return;
            var newFontDesc = font.getFontDesc();
            var newFontString = newFontDesc.toString();
            if (!newFontString.equals(settings.getString("font"))) {
                settings.setString("font", newFontString);
            }
        };

        font.onNotify("font-desc", aaa);

        var transitionString = settings.getString("transition");
        int initialIndex = TRANSITION_IDS.indexOf(transitionString);
        if (initialIndex == -1) initialIndex = 0; // Default to "none"
        transition.setSelected(initialIndex);

        GObject.NotifyCallback onNotifySliderCallback = (ParamSpec paramSpec) -> {
            if (!paramSpec.getName().equals("selected")) return;

            int newIndex = transition.getSelected();
            if (newIndex >= 0 && newIndex < TRANSITION_IDS.size()) {
                var newTransitionString = TRANSITION_IDS.get(newIndex);
                if (!newTransitionString.equals(settings.getString("transition"))) {
                    settings.setString("transition", newTransitionString);
                }
            }
        };

        transition.onNotify("selected", onNotifySliderCallback);
    }

    public HypersonicSettings(HypersonicMainWindow win) {
        setTransientFor(win);
    }
}

