package dev.kofeychi.polygonts.script.events.data;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.IForgeRegistry;

public class ForgeRegistryEventData extends EventData {
    IForgeRegistry<?> registry;

    public ForgeRegistryEventData(Class<? extends Event> eventType, IForgeRegistry<?> registry) {
        super(eventType);
        this.registry = registry;
    }

    @Override
    public boolean fire(Object... args) {
        return args[0] instanceof IForgeRegistry && args[0].equals(this.registry);
    }
}
