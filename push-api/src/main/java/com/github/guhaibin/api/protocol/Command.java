package com.github.guhaibin.api.protocol;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public enum Command {

    HEART_BEAT(0),
    LOGIN(1),   // 登录
    LOGOUT(2),  // 退出
    MESSAGE(3), // 推送消息
    ACK(5),
    CHECK_ONLINE(6),
    UNKNOWN(-1);


    Command(int cmd){
        this.cmd = (byte)cmd;
    }

    public final byte cmd;

    public static Command toCMD(byte b){
        Command[] values = values();
        if (b >= 0 && b < values.length - 1){
            return values[b];
        }
        return UNKNOWN;
    }
}
