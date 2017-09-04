package com.github.guhaibin.utils.thread.pool;

import com.github.guhaibin.api.spi.Spi;
import com.github.guhaibin.api.spi.common.ExecutorFactory;
import com.github.guhaibin.utils.thread.NamedThreadFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
@Spi
public class DefaultExecutorFactory implements ExecutorFactory {



    @Override
    public Executor get(String name) {
        return new ScheduledThreadPoolExecutor(2, new NamedThreadFactory(name));
    }
}
