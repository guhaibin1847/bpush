package com.github.guhaibin.utils.event;

import com.github.guhaibin.api.event.Event;
import com.github.guhaibin.utils.thread.pool.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class EventBus {

    private static final Logger LOG = LoggerFactory.getLogger(EventBus.class);

    public static EventBus I = new EventBus();

    private Map<String, Set<EventHandler>> handlers = new ConcurrentHashMap<>();
    private Executor executor = ThreadPoolManager.I.getEventBusExecutor();

    private EventBus(){}


    public void fire(final Event event){
        Class clazz = event.getClass();
        Set<EventHandler> set = handlers.get(clazz.getName());
        if (set == null){
            LOG.warn("can not find handlers for event: '{}'", event);
            return ;
        }
        executor.execute(() -> set.forEach(h -> h.handle(event)));
    }

    public void register(final EventHandler handler){
        String eventClassName = getActualEventClassName(handler.getClass());
        if (eventClassName == null){
            LOG.warn("can not find actual event class for handler: '{}'", handler);
            return;
        }
        System.out.println(eventClassName);
        handlers.computeIfAbsent(eventClassName, k -> new CopyOnWriteArraySet<>()).add(handler);
    }

    private String getActualEventClassName(Class clazz){
        if (clazz == Object.class){
            return null;
        }
        Type[] types = clazz.getGenericInterfaces();
        for(Type t : types){
            if (t instanceof ParameterizedType){
                ParameterizedType ptype = (ParameterizedType)t;
                Class tClazz = (Class) ptype.getRawType();
                if (tClazz == EventHandler.class){
                    Type actualType = ptype.getActualTypeArguments()[0];
                    return actualType.getTypeName();
                }
            }
        }
        return getActualEventClassName(clazz.getSuperclass());
    }
}
