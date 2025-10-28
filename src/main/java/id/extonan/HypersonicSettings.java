package id.extonan;

import io.github.jwharm.javagi.gobject.annotations.InstanceInit;
import io.github.jwharm.javagi.gtk.annotations.GtkChild;
import io.github.jwharm.javagi.gtk.annotations.GtkTemplate;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.gnome.gio.Settings;
import org.gnome.gobject.GObject;
import org.gnome.gobject.ParamSpec;
import org.gnome.gtk.*;
import org.gnome.pango.FontDescription;
import org.gnome.pango.FontFace;

import java.util.Arrays;
import java.util.List;

@Slf4j
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

    @SuppressWarnings("unused")
    @InstanceInit
    public void init() {
        settings = new Settings("id.extonan.hypersonic");
        var fontString = settings.getString("font");
        var fontDesc = FontDescription.fromString(fontString);
        font.setFontDesc(fontDesc);
        NotifyCallback onFontDescChange = (ParamSpec paramSpec) -> {
            if (!paramSpec.getName().equals("font-desc")) return;
            var newFontDesc = font.getFontDesc();
            var newFontString = newFontDesc.toString();
            if (!newFontString.equals(settings.getString("font"))) {
                settings.setString("font", newFontString);
            }
        };

        font.onNotify("font-desc", onFontDescChange);

        var transitionString = settings.getString("transition");
        int initialIndex = TRANSITION_IDS.indexOf(transitionString);
        if (initialIndex == -1) initialIndex = 0; // Default to "none"
        transition.setSelected(initialIndex);

        NotifyCallback onTransitionChange = (ParamSpec paramSpec) -> {
            if (!paramSpec.getName().equals("selected")) return;

            int newIndex = transition.getSelected();
            if (newIndex >= 0 && newIndex < TRANSITION_IDS.size()) {
                var newTransitionString = TRANSITION_IDS.get(newIndex);
                if (!newTransitionString.equals(settings.getString("transition"))) {
                    settings.setString("transition", newTransitionString);
                }
            }
        };

        transition.onNotify("selected", onTransitionChange);

        var fontDialog = font.getDialog();
        CustomFilterFunc monospaceFilterFunc = (GObject item) -> {
            if (item instanceof FontFace face) {
                // Return true only if the font is monospace
                return face.getFamily().isMonospace();
            }
            log.warn("{} is not a FontFace", item.getClass().getName());
            return false;
        };
        var monospaceFilter = new CustomFilter(monospaceFilterFunc);
        fontDialog.setFilter(monospaceFilter);
    }

    public HypersonicSettings(HypersonicMainWindow win) {
        setTransientFor(win);
    }
}

