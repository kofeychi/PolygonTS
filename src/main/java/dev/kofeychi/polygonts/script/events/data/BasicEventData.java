package dev.kofeychi.polygonts.script.events.data;

import net.minecraftforge.eventbus.api.Event;

public class BasicEventData extends EventData {
    public BasicEventData(Class<? extends Event> eventType) {
        super(eventType);
    }

    @Override
    public boolean fire(Object... args) {
        return true;
    }
}
