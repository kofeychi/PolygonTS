package dev.kofeychi.polygonts.util;



import net.minecraftforge.eventbus.api.EventPriority;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(value = RUNTIME)
@Target(value = METHOD)
public @interface SubscribeEventForge {
    EventPriority priority() default EventPriority.NORMAL;
    boolean receiveCanceled() default false;
}
