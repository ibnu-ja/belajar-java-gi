package id.extonan;

import io.github.jwharm.javagi.base.GErrorException;
import org.gnome.gio.Resource;

@SuppressWarnings("java:S1118")
public class Hypersonic {

    static void main(String[] args) throws GErrorException {
        var resource = Resource.load("src/main/resources/hypersonicapp.gresource");
        resource.resourcesRegister();

        new HypersonicApp().run(args);
    }
}
