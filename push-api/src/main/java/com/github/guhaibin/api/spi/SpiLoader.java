package com.github.guhaibin.api.spi;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class SpiLoader {

    private static final Map<String, Object> CACHE = new ConcurrentHashMap<>();

    public static <T> T load(Class<T> clazz){
        return load(clazz, null);
    }

    public static <T> T load(Class<T> clazz, String name){
        String key = clazz.getName();
        if (CACHE.containsKey(key)){
            Object o =  CACHE.get(key);
            if (clazz.isInstance(o)){
                return (T)o;
            }
        }
        T t = load0(clazz, name);
        CACHE.put(key, t);
        return t;
    }


    public static <T> T load0(Class<T> clazz, String name){
        ServiceLoader<T> loaders = ServiceLoader.load(clazz);

        T t = filter(loaders, name);
        if (t == null){
            loaders = ServiceLoader.load(clazz, SpiLoader.class.getClassLoader());
            t = filter(loaders, name);
        }

        if (t != null){
            return t;
        }else {
            throw new IllegalStateException("can not find META-INF/services/" + clazz.getName() + " on classpath");
        }

    }

    private static <T> T filter(ServiceLoader<T> loaders, String name){
        if (StringUtils.isNotBlank(name)){
            Iterator<T> iterator = loaders.iterator();
            while (iterator.hasNext()){
                T t = iterator.next();
                if (name.equals(t.getClass().getSimpleName())){
                    return t;
                }
            }
        } else {
            List<T> ts = new ArrayList<>(5);
            Iterator<T> iterator = loaders.iterator();
            while (iterator.hasNext()){
                ts.add(iterator.next());
            }
            if (ts.size() > 1) {
                ts.sort((t1, t2) -> {
                    Spi spi1 = t1.getClass().getAnnotation(Spi.class);
                    Spi spi2 = t2.getClass().getAnnotation(Spi.class);
                    int o1 = spi1.order();
                    int o2 = spi2.order();

                    return o1 - o2;
                });
            }

            if (ts.size() > 0)
                return ts.get(0);
        }

        return null;
    }

}
