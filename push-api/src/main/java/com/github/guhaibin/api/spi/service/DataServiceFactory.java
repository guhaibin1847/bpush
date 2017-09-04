package com.github.guhaibin.api.spi.service;

import com.github.guhaibin.api.spi.Factory;
import com.github.guhaibin.api.spi.SpiLoader;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public interface DataServiceFactory extends Factory<DataService> {

    static DataService create(){
        return SpiLoader.load(DataServiceFactory.class).get();
    }
}
