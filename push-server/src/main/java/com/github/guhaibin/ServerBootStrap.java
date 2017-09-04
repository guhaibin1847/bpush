package com.github.guhaibin;

import com.github.guhaibin.server.boot.BootChain;
import com.github.guhaibin.server.boot.CacheManagerBoot;
import com.github.guhaibin.server.boot.WebSocketServerBoot;
import io.netty.bootstrap.ServerBootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public final class ServerBootStrap {

    private static final Logger LOG = LoggerFactory.getLogger("BootStrap");
    private BootChain chain;

    public ServerBootStrap(){
        chain = BootChain.chain()
                .setNext(new CacheManagerBoot())
                .setNext(new WebSocketServerBoot())
                .end();
    }

    public void start(){
        chain.start();
    }

    public void stop(){
        chain.stop();
    }

}
