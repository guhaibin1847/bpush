package com.github.guhaibin.api.spi.common;

import com.github.guhaibin.api.spi.Factory;
import com.github.guhaibin.api.spi.SpiLoader;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public interface MQClientFactory extends Factory<MQClient>{

    static MQClient create(){
        return SpiLoader.load(MQClientFactory.class).get();
    }

}
