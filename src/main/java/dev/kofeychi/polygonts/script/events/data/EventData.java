package dev.kofeychi.polygonts.script.events.data;

import net.minecraftforge.eventbus.api.Event;

import java.util.UUID;

public abstract class EventData {
    public final Class<? extends Event> eventType;
    public final UUID uuid;
    public EventData(Class<? extends Event> eventType, UUID uuid) {
        this.eventType = eventType;
        this.uuid = uuid;
    }
    public EventData(Class<? extends Event> eventType) {
        this(eventType, UUID.randomUUID());
    }
    public abstract boolean fire(Object... args);
}
