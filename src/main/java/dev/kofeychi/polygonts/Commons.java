package dev.kofeychi.polygonts;

import dev.kofeychi.polygonts.impl.util.SubscribeEventForge;
import dev.kofeychi.polygonts.impl.util.SubscribeEventGame;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Commons {
    public static void subscribeIfHas(Object that, IEventBus game, Class<?> cls){
        for(Method method : cls.getDeclaredMethods()){
            var agame = method.getAnnotation(SubscribeEventGame.class);
            if(agame != null){
                game.addListener(
                        agame.priority(),
                        agame.receiveCanceled(),
                        (Class<? extends Event>)method.getParameterTypes()[0],
                        event -> {
                            try {
                                if(Modifier.isStatic(method.getModifiers())){
                                    method.invoke(null,event);
                                } else {
                                    method.invoke(that,event);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                );
            }
            var aforge = method.getAnnotation(SubscribeEventForge.class);
            if(aforge != null){
                MinecraftForge.EVENT_BUS.addListener(
                        aforge.priority(),
                        aforge.receiveCanceled(),
                        (Class<? extends Event>)method.getParameterTypes()[0],
                        event -> {
                            try {
                                if(Modifier.isStatic(method.getModifiers())){
                                    method.invoke(null,event);
                                } else {
                                    method.invoke(that,event);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                );
            }
        }
    }
}
