package dev.kofeychi.polygonts.script.events;

import dev.kofeychi.polygonts.script.events.data.BasicEventData;
import dev.kofeychi.polygonts.script.events.data.EventData;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import net.minecraftforge.eventbus.api.Event;

import java.util.UUID;
import java.util.function.Consumer;

public class EventHandler {
    public ObjectArrayList<ObjectObjectImmutablePair<EventData, Consumer<Event>>> listeners = new ObjectArrayList<>();

    public void remove(EventData data) {
        remove(data.uuid);
    }
    public void remove(UUID uuid) {
        listeners.removeIf(p -> p.left().uuid.equals(uuid));
    }

    public EventCancelFunc on(EventData data, Consumer<Event> listener) {
        listeners.push(new ObjectObjectImmutablePair<>(
                data, listener
        ));
        return () -> {
            remove(data.uuid);
        };
    }
    public EventCancelFunc on(Class<? extends Event> eventClass,Consumer<Event> listener) {
        var data = new BasicEventData(eventClass);
        listeners.push(new ObjectObjectImmutablePair<>(
                data, listener
        ));
        return () -> {
            remove(data.uuid);
        };
    }

    public void fire(Class<? extends Event> eventType, Event event) {
        for (ObjectObjectImmutablePair<EventData, Consumer<Event>> pair : listeners) {
            if(pair.left().fire(event)&&pair.left().eventType.equals(eventType)) {
                pair.right().accept(event);
            }
        }
    }
}
