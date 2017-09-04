package com.github.guhaibin.utils.common;

import com.github.guhaibin.api.Config;
import com.github.guhaibin.api.push.User;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.UUID;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class IdGen {

    public static String uuid(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String md5(String str){
        return DigestUtils.md5Hex(str);
    }

    public static String genServerPwd(String username){
        return md5(md5(Config.Authenticate.serverKey + username));
    }

    public static String genPwd(User user){
        String username = user.getUsername();
        switch (user.getUserType()){
            case CLIENT_USER:
                return genServerPwd(username);
            default:
                return "";
        }
    }

    public static String getUserId(User user){
        return user.getUserType() + "_" + user.getUsername();
    }
}
