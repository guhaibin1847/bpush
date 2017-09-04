package com.github.guhaibin.server;

import com.github.guhaibin.api.MessageDispatcher;
import com.github.guhaibin.api.connection.ConnectionManager;
import com.github.guhaibin.api.protocol.Command;
import com.github.guhaibin.api.service.Listener;
import com.github.guhaibin.api.spi.common.CacheManager;
import com.github.guhaibin.api.spi.service.DataService;
import com.github.guhaibin.api.spi.service.DataServiceFactory;
import com.github.guhaibin.cache.RedisCacheManager;
import com.github.guhaibin.netty.connection.ServerConnectionManager;
import com.github.guhaibin.netty.handler.ServerWebSocketHandler;
import com.github.guhaibin.netty.server.NettyWebSocketServer;
import com.github.guhaibin.server.handler.*;
import com.github.guhaibin.utils.thread.pool.ThreadPoolManager;
import io.netty.channel.ChannelPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class WebSocketServer extends NettyWebSocketServer {

    private static final Logger LOG = LoggerFactory.getLogger(WebSocketServer.class);

    private Executor executor = ThreadPoolManager.I.getServerExecutor();
    private MessageDispatcher dispatcher = new MessageDispatcher(executor);
    private DataService dataService = DataServiceFactory.create();
    private ConnectionManager connectionManager = new ServerConnectionManager(true, dataService);


    @Override
    public void init() {
        super.init();
        dispatcher.register(Command.LOGIN, new LoginHandler(dataService));
        dispatcher.register(Command.HEART_BEAT, new HeartbeatHandler());
        dispatcher.register(Command.MESSAGE, new PushMessageHandler(connectionManager, dataService));
        dispatcher.register(Command.ACK, new AckMessageHandler(connectionManager, dataService));
        dispatcher.register(Command.CHECK_ONLINE, new CheckOnlineHandler(dataService));
    }

    @Override
    protected void initPipeline(ChannelPipeline pipeline) {
        super.initPipeline(pipeline);
        pipeline.addLast("server-handler", new ServerWebSocketHandler(dispatcher, connectionManager));
    }

    public void startup(){
        init();
        startInternal(new Listener() {
            @Override
            public void onSuccess(Object... args) {
            }

            @Override
            public void onFailed(Throwable t) {
                t.printStackTrace(System.out);
            }
        });
    }

    public static void main(String[] args){
        WebSocketServer server = new WebSocketServer();
        server.startup();
    }
}
