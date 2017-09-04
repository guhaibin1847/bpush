package com.github.guhaibin.utils;

import com.alibaba.fastjson.JSON;
import com.github.guhaibin.api.push.User;
import com.github.guhaibin.api.push.UserType;
import com.github.guhaibin.api.spi.common.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class Jsons {

    private static final Logger LOG = LoggerFactory.getLogger(Jsons.class);

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return JSON.parseObject(json, clazz);
        }catch (Exception e){
            LOG.error("decode json error, json is '{}'", json, e);
        }
        return null;
    }

    public static String toJson(Object object) {
        try{
            if (object instanceof String){
                return (String) object;
            }
            return JSON.toJSONString(object);
        }catch (Exception e){
            LOG.error("encode json error, bean is '{}'", object, e);
        }
        return null;
    }

    public static <T> T fromJson(byte[] json, Class<T> clazz){
        return fromJson(new String(json, Charset.forName("utf-8")), clazz);
    }

    public static <T> List<T> fromJsonToList(String json, Class<T> clazz){
        try{
            return JSON.parseArray(json, clazz);
        }catch (Exception e){
            LOG.error("decode json to list error. json is '{}'", json, e);
        }
        return null;
    }


}
