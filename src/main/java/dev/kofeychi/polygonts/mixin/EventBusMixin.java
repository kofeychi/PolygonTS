package dev.kofeychi.polygonts.mixin;

import dev.kofeychi.polygonts.PolygonTS;
import net.minecraftforge.eventbus.EventBus;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBusInvokeDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EventBus.class,remap = false)
public class EventBusMixin {
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraftforge/eventbus/ListenerList;getListeners(I)[Lnet/minecraftforge/eventbus/api/IEventListener;"),method = "post(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraftforge/eventbus/api/IEventBusInvokeDispatcher;)Z",cancellable = true)
    private void post(Event event, IEventBusInvokeDispatcher wrapper, CallbackInfoReturnable<Boolean> cir){
        PolygonTS.scriptManager.eventHandler.fire(event.getClass(),event);
        PolygonTS.LOGGER.info(event.getClass().getName());
    }
}
