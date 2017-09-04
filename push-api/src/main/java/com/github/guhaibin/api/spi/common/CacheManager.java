package com.github.guhaibin.api.spi.common;

import java.util.Map;
import java.util.Set;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public interface CacheManager {

    void init();

    void destroy();

    void del(String key);

    void set(String key, Object value);

    void set(String key, Object value, int expireTime);

    <T> T get(String key, Class<T> clazz);

    void hset(String key, String subKey, Object value);

    <T> T hget(String key, String subKey, Class<T> clazz);

    <T> Map<String, T> hget(String key, Class<T> clazz);

    void hrem(String key, String subKey);

    long hincr(String key, String subKey);

    void sadd(String key, Object val);

    boolean sisMember(String key, Object val);

    void srem(String key, Object val);

    Set<String> sget(String key);

    <T> Set<T> sget(String key, Class<T> clazz);

    void zadd(String key, Object val, double score);

    boolean zisMember(String key, Object val);

    void zrem(String key, Object val);

    void zrem(String key, double min, double max);

    void zreplace(String key, Object val, double score);

    double zincr(String key, Object val, double score);

    <T> Set<T> zget(String key, double min, double max, Class<T> clazz);

    Set<String> zget(String key, double min, double max);
}
