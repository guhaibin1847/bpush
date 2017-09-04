package com.github.guhaibin.mq;

import com.github.guhaibin.api.spi.Spi;
import com.github.guhaibin.api.spi.common.MQClient;
import com.github.guhaibin.api.spi.common.MQClientFactory;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
@Spi(order = 1)
public class RedisMQClientFactory implements MQClientFactory {
    @Override
    public MQClient get() {
        return RedisMQClient.I;
    }
}
