package com.github.guhaibin.server.boot;

import com.github.guhaibin.api.spi.common.CacheManager;
import com.github.guhaibin.api.spi.common.CacheManagerFactory;
import com.github.guhaibin.core.service.RedisDataService;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class CacheManagerBoot extends BootJob {

    public CacheManagerBoot(){
        super("redisCacheManagerJob");
    }

    @Override
    void start() {
        CacheManager cacheManager = CacheManagerFactory.create();
        cacheManager.init();
        RedisDataService.me(cacheManager).clearAll();
        startNext();
    }

    @Override
    void stop() {
        stopNext();
        CacheManager cacheManager = CacheManagerFactory.create();
        RedisDataService.me(cacheManager).clearAll();
        cacheManager.destroy();
    }
}
