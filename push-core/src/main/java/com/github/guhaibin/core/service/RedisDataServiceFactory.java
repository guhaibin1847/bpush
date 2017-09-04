package com.github.guhaibin.core.service;

import com.github.guhaibin.api.spi.Spi;
import com.github.guhaibin.api.spi.common.CacheManagerFactory;
import com.github.guhaibin.api.spi.service.DataService;
import com.github.guhaibin.api.spi.service.DataServiceFactory;
import com.github.guhaibin.cache.RedisCacheManager;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
@Spi(order = 1)
public class RedisDataServiceFactory implements DataServiceFactory {

    @Override
    public DataService get() {
        return RedisDataService.me(RedisCacheManager.I);
    }
}
