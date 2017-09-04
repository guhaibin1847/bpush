package com.github.guhaibin.api;

import com.github.guhaibin.api.push.UserType;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public interface Config {

    PropReader prop = init();

    Charset utf8 = Charset.forName("utf-8");
    boolean dev = false;

    static PropReader init(){
        InputStream in = Config.class.getClassLoader().getResourceAsStream("push.properties");
        return PropReader.load(in);
    }

    interface RedisConf {
        String host = "localhost";
        int port = 6379;

        String prefix = "pusher:";
    }

    interface WebSocketConf {
        String host = prop.getString("host", "localhost");
        int port = prop.getInt("port", 18888);
        String wsPath = "/ws";
        int maxRetryCount = 3;
        int clientReconnectInterval = 20;
        int timeout = 180;
        int heartBeatInterval = 60;
        int bossThreadNums = 1;
        int workerThreadNums = 16;
        int maxTimeoutTimes = 3;
    }

    interface ClientConf {
        UserType userType = UserType.CLIENT_USER;
        String username = prop.getString("username", "bean");
        int ackTimeOut = 20;
    }

    interface Authenticate {
        String serverKey = "server123";
    }

    interface MailConf {
        String from = "";
        String host = "";
        String username = "";
        String pwd = "";
        int port = 25;
    }

    interface Alert {
        String[] toList = {"guhaibin1847@gmail.com"};
    }
}
