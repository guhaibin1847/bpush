package com.github.guhaibin.cache;

import com.github.guhaibin.api.spi.Spi;
import com.github.guhaibin.api.spi.common.CacheManager;
import com.github.guhaibin.api.spi.common.CacheManagerFactory;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
@Spi(order = 1)
public class RedisCacheManagerFactory implements CacheManagerFactory {

    @Override
    public CacheManager get() {
        return RedisCacheManager.I;
    }
}
