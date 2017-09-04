package com.github.guhaibin;


/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class JYPusher {



    public static void main(String[] args){
        ServerBootStrap bootstrap = new ServerBootStrap();
        bootstrap.start();
        addShutdownHook(bootstrap);
    }


    private static void addShutdownHook(ServerBootStrap bootstrap){
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            bootstrap.stop();
        }, "jy-pusher-shutdown-hook-thread"));
    }

}
