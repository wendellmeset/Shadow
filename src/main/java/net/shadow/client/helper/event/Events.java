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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Events {
    static final List<ListenerEntry> entries = new CopyOnWriteArrayList<>();

    public static ListenerEntry registerEventHandler(int uniqueId, EventType event, Consumer<? extends Event> handler) {
        return registerEventHandler(uniqueId, event, handler, Events.class);
    }

    public static ListenerEntry registerEventHandler(int uniqueId, EventType event, Consumer<? extends Event> handler, Class<?> owner) {
        if (entries.stream().noneMatch(listenerEntry -> listenerEntry.id == uniqueId)) {
            ListenerEntry le = new ListenerEntry(uniqueId, event, handler, owner);
            entries.add(le);
            return le;
        } else {
            ShadowMain.log(Level.WARN, uniqueId + " tried to register " + event.name() + " multiple times");
            return entries.stream().filter(listenerEntry -> listenerEntry.id == uniqueId).findFirst().orElseThrow();
        }
    }

    public static void unregister(int id) {
        entries.removeIf(listenerEntry -> listenerEntry.id == id);
    }

    public static ListenerEntry registerEventHandler(EventType event, Consumer<? extends Event> handler) {
        return registerEventHandler((int) Math.floor(Math.random() * 0xFFFFFF), event, handler);
    }

    public static void unregisterEventHandlerClass(Object instance) {
        for (ListenerEntry entry : new ArrayList<>(entries)) {
            if (entry.owner.equals(instance.getClass())) {
                ShadowMain.log(Level.INFO, "Unregistering " + entry.type + ":" + entry.id);
                entries.remove(entry);
            }
        }
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

                        ListenerEntry l = registerEventHandler((instance.getClass().getName() + declaredMethod.getName()).hashCode(), ev.type(), event -> {
                            try {
                                declaredMethod.invoke(instance, event);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }, instance.getClass());
                        ShadowMain.log(Level.INFO, "Registered event handler " + declaredMethod + " with id " + l.id);
                    }
                }
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static boolean fireEvent(EventType event, Event argument) {
        List<ListenerEntry> le = entries.stream().filter(listenerEntry -> listenerEntry.type == event).toList();
        if (le.size() == 0) {
//            ShadowMain.log(Level.INFO, "no one cares about "+event+" so we're gonna skip it");
            return false;
        }
        for (ListenerEntry entry : le) {
            ((Consumer) entry.eventListener()).accept(argument);
        }
        return argument.isCancelled();
    }

    record ListenerEntry(int id, EventType type, Consumer<? extends Event> eventListener, Class<?> owner) {
    }
}
