package com.github.guhaibin.api.protocol;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public enum Flags {
    ACK((byte)1);


    Flags(byte flag){
        this.flag = flag;
    }

    public final byte flag;
}
