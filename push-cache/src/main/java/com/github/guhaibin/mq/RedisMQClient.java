package com.github.guhaibin.mq;

import com.github.guhaibin.api.spi.common.MQClient;
import com.github.guhaibin.api.spi.common.MQMessageReceiver;
import com.github.guhaibin.cache.RedisCacheManager;
import com.github.guhaibin.utils.thread.pool.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executor;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class RedisMQClient implements MQClient {

    private static final Logger LOG = LoggerFactory.getLogger(RedisMQClient.class);

    public static final RedisMQClient I = new RedisMQClient();

    private final Map<String, List<MQMessageReceiver>> subscribers = new TreeMap<>();
    private final RedisCacheManager redisClient = RedisCacheManager.I;
    private final Subscriber subscriber = new Subscriber();

    private RedisMQClient(){}

    @Override
    public void subscribe(String topic, MQMessageReceiver messageReceiver) {
        subscribers.computeIfAbsent(topic, k -> new ArrayList<>()).add(messageReceiver);
        redisClient.subscribe(subscriber, topic);
    }

    @Override
    public void publish(String topic, Object message) {
        redisClient.publish(topic, message);
    }

    private class Subscriber extends JedisPubSub {
        private Executor executor = ThreadPoolManager.I.getRedisExecutor();
        @Override
        public void onMessage(String channel, String message){
            List<MQMessageReceiver> receivers = subscribers.get(channel);
            if (receivers == null){
                LOG.info("can not find receiver for channel: '{}'", channel);
                return;
            }
            executor.execute(() -> receivers.forEach(r -> r.onReceive(channel, message)));
        }
    }
}
