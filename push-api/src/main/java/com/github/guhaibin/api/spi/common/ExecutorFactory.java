package com.github.guhaibin.api.spi.common;

import com.github.guhaibin.api.spi.SpiLoader;

import java.util.concurrent.Executor;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public interface ExecutorFactory  {

    String MQ = "mq";
    String EVENT_BUS = "event-bus";


    Executor get(String name);

    static ExecutorFactory create(){
        return SpiLoader.load(ExecutorFactory.class);
    }
}
