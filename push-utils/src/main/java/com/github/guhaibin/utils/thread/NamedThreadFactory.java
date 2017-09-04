package com.github.guhaibin.utils.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class NamedThreadFactory implements ThreadFactory {

    private static final AtomicInteger count = new AtomicInteger(0);
    private final String prefix;
    private final ThreadGroup group;

    public NamedThreadFactory(){
        this(ThreadNames.CP);
    }

    public NamedThreadFactory(String prefix){
        this.prefix = prefix;
        this.group = Thread.currentThread().getThreadGroup();
    }

    @Override
    public Thread newThread(Runnable r) {
        return newThread("", r);
    }

    public Thread newThread(String name, Runnable r){
        Thread thread = new Thread(group, r,
                prefix + "-" + count.getAndIncrement() + "-" + name);
        thread.setDaemon(false);
        return thread;
    }

    public static NamedThreadFactory me(){
        return new NamedThreadFactory();
    }

    public static NamedThreadFactory me(String prefix){
        return new NamedThreadFactory(prefix);
    }
}
