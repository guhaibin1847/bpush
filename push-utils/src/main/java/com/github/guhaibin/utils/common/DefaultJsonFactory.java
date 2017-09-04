package com.github.guhaibin.utils.common;

import com.github.guhaibin.api.spi.Spi;
import com.github.guhaibin.api.spi.common.Json;
import com.github.guhaibin.api.spi.common.JsonFactory;
import com.github.guhaibin.utils.Jsons;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
@Spi
public class DefaultJsonFactory implements JsonFactory, Json {
    @Override
    public Json get() {
        return this;
    }

    @Override
    public <T> T fromJson(String json, Class<T> clazz) {
        return Jsons.fromJson(json, clazz);
    }

    @Override
    public String toJson(Object object) {
        return Jsons.toJson(object);
    }
}
