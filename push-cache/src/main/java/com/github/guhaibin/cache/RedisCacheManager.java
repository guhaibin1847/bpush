package com.github.guhaibin.cache;

import com.github.guhaibin.api.Config;
import com.github.guhaibin.api.spi.common.CacheManager;
import com.github.guhaibin.api.spi.common.Json;
import com.github.guhaibin.utils.Jsons;
import com.github.guhaibin.utils.thread.pool.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class RedisCacheManager implements CacheManager {

    private static final Logger LOG = LoggerFactory.getLogger(RedisCacheManager.class);

    public static final RedisCacheManager I = new RedisCacheManager();

    private JedisPool pool;

    private RedisCacheManager(){}

    @Override
    public void init() {
        pool = new JedisPool(new JedisPoolConfig(),
                             Config.RedisConf.host,
                             Config.RedisConf.port
                            );
    }

    @Override
    public void destroy() {
        if (pool != null) {
            pool.close();
        }
    }

    private <R> R call(Function<Jedis, R> func, R def){
        try(Jedis jedis = pool.getResource()){
            return func.apply(jedis);
        }catch (Exception e){
            LOG.error("redis execute error", e);
        }
        return def;
    }

    private void call(Consumer<Jedis> consumer){
        try(Jedis jedis = pool.getResource()){
            consumer.accept(jedis);
        }catch (Exception e){
            LOG.error("redis execute error", e);
        }
    }

    @Override
    public void del(String key) {
        call(jedis -> jedis.del(key));
    }

    @Override
    public void set(String key, Object value) {
        call(jedis -> jedis.set(key, Jsons.toJson(value)));
    }

    @Override
    public void set(String key, Object value, int expireTime) {
        call(jedis -> {
            jedis.set(key, Jsons.toJson(value));
            if (expireTime > 0) {
                jedis.expire(key, expireTime);
            }
        });
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        String value = call(jedis -> jedis.get(key), null);
        if (value != null){
            if (clazz == String.class){
                return (T)value;
            }else {
                return Jsons.fromJson(value, clazz);
            }
        }
        return null;
    }

    /******************* map ************************/

    @Override
    public void hset(String key, String subKey, Object value) {
        call(jedis -> jedis.hset(key, subKey, Jsons.toJson(value)));
    }

    @Override
    public <T> T hget(String key, String subKey, Class<T> clazz) {
        String value = call(jedis -> jedis.hget(key, subKey), null);
        if (value != null){
            if (clazz == String.class){
                return (T)value;
            }else {
                return Jsons.fromJson(value, clazz);
            }
        }
        return null;
    }

    public void hrem(String key, String subKey){
        call(jedis -> jedis.hdel(key, subKey));
    }

    public long hincr(String key, String subKey){
        return call(jedis -> jedis.hincrBy(key, subKey, 1L), 0L);
    }

    @Override
    public <T> Map<String, T> hget(String key, Class<T> clazz) {
        Map<String, String> map = call(jedis -> jedis.hgetAll(key), Collections.emptyMap());
        if (map.isEmpty()){
            return Collections.emptyMap();
        }
        Map<String, T> newMap = new HashMap<>(map.size());
        map.forEach((k, v) -> {
            if (clazz == String.class){
                newMap.put(k, (T)v);
            }else {
                newMap.put(k, Jsons.fromJson(v, clazz));
            }
        });
        return newMap;
    }

    /******************* set ************************/

    public void sadd(String key, Object val){
        call(jedis -> jedis.sadd(key, Jsons.toJson(val)));
    }


    public boolean sisMember(String key, Object val){
        return call(jedis -> jedis.sismember(key, Jsons.toJson(val)), false);
    }

    public Set<String> sget(String key){
        return call(jedis -> jedis.smembers(key), new HashSet<>());
    }

    public <T> Set<T> sget(String key, Class<T> clazz){
        Set<String> set = sget(key);
        if (clazz == String.class){
            return (Set<T>)set;
        }else {
            return set.stream().map(v -> Jsons.fromJson(v, clazz)).collect(Collectors.toSet());
        }
    }

    public void srem(String key, Object val){
        call(jedis -> jedis.srem(key, Jsons.toJson(val)));
    }

    /******************* zset ************************/
    public void zadd(String key, Object val, double score) {
        call(jedis -> jedis.zadd(key, score, Jsons.toJson(val)));
    }

    public void zrem(String key, Object val){
        call(jedis -> jedis.zrem(key, Jsons.toJson(val)));
    }

    public void zrem(String key, double min, double max){
        call(jedis -> jedis.zremrangeByScore(key, min, max));
    }

    public void zreplace(String key, Object val, double score){
        call(jedis -> {
            Transaction t = jedis.multi();
            jedis.zrem(key, Jsons.toJson(val));
            jedis.zadd(key, score, Jsons.toJson(val));
            t.exec();
        });
    }

    public double zincr(String key, Object val, double score){
        return call(jedis -> jedis.zincrby(key, score, Jsons.toJson(val)), 0.);
    }

    public <T> Set<T> zget(String key, double min, double max, Class<T> clazz){
        Set<String> value = call(jedis -> jedis.zrangeByScore(key, min, max), null);
        if (value == null) {
            return null;
        }else {
            if (clazz == String.class) {
                return value.stream().map(v -> (T) v).collect(Collectors.toSet());
            }else {
                return value.stream().map(v -> Jsons.fromJson(v, clazz)).collect(Collectors.toSet());
            }
        }
    }

    public Set<String> zget(String key, double min, double max){
        return call(jedis -> jedis.zrangeByScore(key, min, max), new HashSet<>());
    }

    public boolean zisMember(String key, Object val){
        return call(jedis -> jedis.zrank(key, Jsons.toJson(val)) != null, false);
    }

    /******************* pub/sub ************************/

    public void publish(String channel, Object message){
        String msg = message instanceof String ? (String) message : Jsons.toJson(message);
        call(jedis -> jedis.publish(channel, msg));
    }

    public void subscribe(JedisPubSub pubSub, String channel){
        ThreadPoolManager.I.newThread(channel, () -> {
            call(jedis ->
                jedis.subscribe(pubSub, channel)
            );
        }).start();
    }
}
