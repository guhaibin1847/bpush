package com.github.guhaibin.netty.server;

import com.github.guhaibin.api.Config;
import com.github.guhaibin.api.service.BaseService;
import com.github.guhaibin.api.service.Listener;
import com.github.guhaibin.api.service.Server;
import com.github.guhaibin.netty.handler.ClientReconnectHandler;
import com.github.guhaibin.netty.handler.ClientWebSocketHandler;
import com.github.guhaibin.netty.handler.ServerWebSocketHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class NettyWebSocketServer extends BaseService implements Server {

    private static final Logger LOG = LoggerFactory.getLogger(NettyWebSocketServer.class);

    private Integer port = Config.WebSocketConf.port;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    @Override
    public void startInternal(Listener listener){
        bossGroup = new NioEventLoopGroup(Config.WebSocketConf.bossThreadNums);
        workerGroup = new NioEventLoopGroup(Config.WebSocketConf.workerThreadNums);
        ServerBootstrap b = new ServerBootstrap();

        b.group(bossGroup, workerGroup);
        b.channel(NioServerSocketChannel.class);
        if (Config.dev) {
            b.handler(new LoggingHandler(LogLevel.INFO));
        }
        b.childHandler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                initPipeline(ch.pipeline());
            }
        });

        b.bind(port).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()){
                    LOG.info("server start up!");
                    if (listener != null) {
                        listener.onSuccess(port);
                    }
                }else {
                    LOG.error("server start up error");
                    if (listener != null) {
                        listener.onFailed(channelFuture.cause());
                    }
                }
            }
        });
    }

    protected void initPipeline(ChannelPipeline pipeline){
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new WebSocketServerCompressionHandler());
        pipeline.addLast(new WebSocketServerProtocolHandler(Config.WebSocketConf.wsPath,
                                    null, true));
    }

    @Override
    protected void stopInternal(Listener listener) {
        if (this.bossGroup != null) {
            this.bossGroup.shutdownGracefully();
        }
        if (this.workerGroup != null) {
            this.workerGroup.shutdownGracefully();
        }
    }

}
