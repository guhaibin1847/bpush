package com.github.guhaibin.api.push;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public enum UserType {
    CLIENT_USER((byte)0),
    TAG((byte)1),
    PUSH_SERVER((byte)2),
    UNKNOWN((byte)-1);

    UserType(byte typeVal){
        this.typeVal = typeVal;
    }

    public final byte typeVal;

    public static UserType to(int val){
        UserType[] types = values();
        if (val >= 0 && val < types.length - 1){
            return types[val];
        }
        return UNKNOWN;
    }

    public static UserType to(byte val){
        return to((int)val);
    }
}
