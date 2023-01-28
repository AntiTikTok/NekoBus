package com.antitiktok.nekobus;

import com.antitiktok.nekobus.exception.IllegalClassException;
import com.antitiktok.nekobus.type.Priority;
import com.antitiktok.nekobus.type.ReceiveEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Neko Event Bus
 *
 * @author KamiSkidder
 */
public class NekoBus {
    private final HashMap<Priority, HashMap<Type, List<Listener>>> listeners;

    public NekoBus() {
        listeners = new HashMap<>();
        for (Priority priority : Priority.values()) {
            listeners.put(priority, new HashMap<>());
        }
    }

    /**
     * Register class to the event bus
     *
     * @param target target class
     */
    public void register(Object target) {
        Class<?> getMethodsFromClass = target.getClass();
        for (Method method : getMethodsFromClass.getDeclaredMethods()) {
            ReceiveEvent annotation = method.getAnnotation(ReceiveEvent.class);
            if (annotation == null)
                continue;

            int modifier = method.getModifiers();
            if (Modifier.isStatic(modifier))
                throw new IllegalClassException("Static class does not support");
            if (Modifier.isPrivate(modifier) || Modifier.isProtected(modifier))
                throw new IllegalClassException("Event listener must be public");
            if (Modifier.isNative(modifier) || Modifier.isInterface(modifier))
                throw new IllegalClassException("Unsupported class type");
            if (method.getGenericParameterTypes().length != 1)
                throw new IllegalClassException("Parameters must be one");

            Type type = method.getGenericParameterTypes()[0];
            if (type.equals(NekoEvent.class))
                throw new IllegalArgumentException("Parameter must be extends \"NekoEvent\"");

            Listener listener = new Listener(target, method);
            HashMap<Type, List<Listener>> types = this.listeners.get(annotation.priority());
            if (!types.containsKey(type))
                types.put(type, new ArrayList<>());
            types.get(type).add(listener);
        }
    }

    /**
     * Unregister class from the event bus
     *
     * @param target target class
     */
    public void unregister(Object target) {
        Class<?> getMethodsFromClass = target.getClass();
        for (Method method : getMethodsFromClass.getDeclaredMethods()) {
            ReceiveEvent annotation = method.getAnnotation(ReceiveEvent.class);
            if (annotation == null)
                continue;

            HashMap<Type, List<Listener>> types = this.listeners.get(annotation.priority());
            for (Type type : types.keySet()) {
                List<Listener> listeners = types.get(type);
                for (int ii = 0; ii < listeners.size(); ii++) {
                    Listener listener = listeners.get(ii);
                    // reference check
                    boolean equals = listener.instance == target;
                    if (equals && listener.method.equals(method)) {
                        System.out.println(method.getName());
                        listeners.remove(ii);
                    }
                }

                if (listeners.isEmpty())
                    types.remove(types.get(type));
            }
        }
    }

    /**
     * Post event to the event bus
     *
     * @param event event to post
     */
    public void post(NekoEvent event) {
        for (Priority priority : Priority.values()) {
            try {
                process(priority, event);
            } catch (InvocationTargetException | IllegalAccessException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void process(Priority priority, NekoEvent event) throws InvocationTargetException, IllegalAccessException {
        HashMap<Type, List<Listener>> types = listeners.get(priority);
        Class<?> clazz = event.getClass();
        if (!types.containsKey(clazz))
            return;

        for (Listener listener : types.get(clazz)) {
            listener.method.invoke(listener.instance, event);
        }
    }

    private static class Listener {
        public Object instance;
        public Method method;

        public Listener(Object instance, Method method) {
            this.instance = instance;
            this.method = method;
        }
    }
}
