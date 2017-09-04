package com.github.guhaibin.server.boot;

import com.github.guhaibin.server.WebSocketServer;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class WebSocketServerBoot extends BootJob {

    private WebSocketServer server;

    public WebSocketServerBoot() {
        super("WebSocketServerJob");
    }

    @Override
    void start() {
        server = new WebSocketServer();
        server.startup();
        startNext();
    }

    @Override
    void stop() {
        stopNext();
        server.stop();
    }
}
