package com.github.guhaibin.utils.thread.pool;

import com.github.guhaibin.api.spi.common.ExecutorFactory;
import com.github.guhaibin.utils.thread.NamedThreadFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class ThreadPoolManager {

    public static ThreadPoolManager I = new ThreadPoolManager();

    private final NamedThreadFactory threadFactory = NamedThreadFactory.me();
    private final ExecutorFactory executorFactory = ExecutorFactory.create();
    private final Map<String, Executor> pools = new ConcurrentHashMap<>();

    public Thread newThread(String name, Runnable runnable){
        return threadFactory.newThread(name, runnable);
    }

    public Executor getRedisExecutor(){
        return pools.computeIfAbsent("redis-mq", s -> executorFactory.get("mq"));
    }

    public Executor getEventBusExecutor(){
        return pools.computeIfAbsent("event-bus", executorFactory::get);
    }

    public Executor getServerExecutor(){
        return pools.computeIfAbsent("server", executorFactory::get);
    }

    public Executor getClientExecutor(){
        return pools.computeIfAbsent("client", executorFactory::get);
    }


}
