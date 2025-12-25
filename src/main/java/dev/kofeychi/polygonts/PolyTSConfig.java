package dev.kofeychi.polygonts;

import dev.kofeychi.polygonts.impl.util.config.handler.ConfigHolder;
import dev.kofeychi.polygonts.impl.util.config.handler.GsonSerializer;

public class PolyTSConfig {
    public static final ConfigHolder<PolyTSConfig> HOLDER = new ConfigHolder<>(
            PolyTSConfig.class,
            PolyTSPaths.POLYTS.resolve("polyts.json"),
            GsonSerializer::new
    );
}
