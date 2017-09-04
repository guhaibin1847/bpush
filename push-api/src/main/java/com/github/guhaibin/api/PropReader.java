package com.github.guhaibin.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public final class PropReader {

    private static final Logger LOG = LoggerFactory.getLogger(PropReader.class);

    private Properties prop;
    private PropReader(){
        prop = new Properties();
    }

    public static PropReader load(InputStream in){
        PropReader p = new PropReader();
        if (in != null){
            try {
                p.prop.load(in);
            } catch (IOException e) {
                LOG.error("load properties error", e);
            }
        }
        return p;
    }

    public String getString(String key, String def){
        return prop.getProperty(key, def);
    }

    public int getInt(String key, int def){
        String s = prop.getProperty(key);
        if (s == null){
            return def;
        }
        return Integer.parseInt(s);
    }

    public double getDouble(String key, double def){
        String s = prop.getProperty(key);
        if (s == null){
            return def;
        }
        return Double.parseDouble(s);
    }


}
