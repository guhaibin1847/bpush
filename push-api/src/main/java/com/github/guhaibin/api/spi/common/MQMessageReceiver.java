package com.github.guhaibin.api.spi.common;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public interface MQMessageReceiver {
    void onReceive(String topic, Object message);
}
