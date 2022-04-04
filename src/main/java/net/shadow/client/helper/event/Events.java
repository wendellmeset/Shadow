/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.helper.event;

import net.shadow.client.ShadowMain;
import net.shadow.client.helper.event.events.base.Event;
import org.apache.logging.log4j.Level;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Events {

    static final Map<EventType, List<Consumer<? extends Event>>> HANDLERS = new HashMap<>();

    public static void registerEventHandler(EventType event, Consumer<? extends Event> handler) {
        if (!HANDLERS.containsKey(event)) {
            HANDLERS.put(event, new ArrayList<>());
        }
        HANDLERS.get(event).add(handler);
    }

    public static void registerEventHandlerClass(Object instance) {
        for (Method declaredMethod : instance.getClass().getDeclaredMethods()) {
            for (Annotation declaredAnnotation : declaredMethod.getDeclaredAnnotations()) {
                if (declaredAnnotation.annotationType() == EventListener.class) {
                    EventListener ev = (EventListener) declaredAnnotation;
                    Class<?>[] params = declaredMethod.getParameterTypes();
                    if (params.length != 1 || !Event.class.isAssignableFrom(params[0])) {
                        ShadowMain.log(Level.ERROR, "Event handler " + declaredMethod.getName() + "(" + Arrays.stream(params).map(Class::getSimpleName).collect(Collectors.joining(", ")) + ") -> " + declaredMethod.getReturnType().getName() + " from " + instance.getClass().getName() + " is malformed, skipping");
                    } else {
                        declaredMethod.setAccessible(true);
                        registerEventHandler(ev.type(), event -> {
                            try {
                                declaredMethod.invoke(instance, event);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        });
                    }


                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean fireEvent(EventType event, Event argument) {
        if (HANDLERS.containsKey(event)) {
            for (Consumer handler : HANDLERS.get(event)) {
                handler.accept(argument);
            }
        }
        return argument.isCancelled();
    }
}
