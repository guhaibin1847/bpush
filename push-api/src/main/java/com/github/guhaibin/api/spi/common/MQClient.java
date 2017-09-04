package com.github.guhaibin.api.spi.common;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public interface MQClient {

    void subscribe(String topic, MQMessageReceiver messageReceiver);
    void publish(String topic, Object message);

}
