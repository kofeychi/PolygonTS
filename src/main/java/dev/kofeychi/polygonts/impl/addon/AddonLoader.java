package dev.kofeychi.polygonts.impl.addon;

import java.util.ServiceLoader;

public class AddonLoader {
    public static final ServiceLoader<PolyformAddon> ADDON_LOADER = ServiceLoader.load(PolyformAddon.class);

    public static void init(){
        ADDON_LOADER.iterator().forEachRemaining(addon->{

        });
    }
}
