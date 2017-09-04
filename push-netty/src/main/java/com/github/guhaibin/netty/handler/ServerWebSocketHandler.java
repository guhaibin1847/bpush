package com.github.guhaibin.netty.handler;

import com.github.guhaibin.api.MessageDispatcher;
import com.github.guhaibin.api.connection.Connection;
import com.github.guhaibin.api.connection.ConnectionManager;
import com.github.guhaibin.api.protocol.Packet;
import com.github.guhaibin.api.spi.service.DataService;
import com.github.guhaibin.netty.connection.NettyConnection;
import com.github.guhaibin.utils.thread.pool.ThreadPoolManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
@ChannelHandler.Sharable
public class ServerWebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static final Logger LOG = LoggerFactory.getLogger(ServerWebSocketHandler.class);
    private MessageDispatcher dispatcher;
    private ConnectionManager connectionManager;

    public ServerWebSocketHandler(MessageDispatcher dispatcher,
                                  ConnectionManager connectionManager){
        this.dispatcher = dispatcher;
        this.connectionManager = connectionManager;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        Connection connection = connectionManager.get(ctx.channel());
        if (connection == null){
            LOG.error("client has not active, channel is '{}'", ctx.channel());
            return;
        }
        Packet packet = Packet.fromFrame(msg);
        dispatcher.receive(packet, connection);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Connection connection = new NettyConnection();
        connection.init(ctx.channel());
        connectionManager.put(connection);
        LOG.trace("connection active, connection is '{}', active connection count is '{}'",
                    connection, connectionManager.count());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        Connection connection = connectionManager.remove(ctx.channel());

        LOG.trace("connection in active, connection is '{}', active connection count is '{}'",
                connection, connectionManager.count());
    }
}
