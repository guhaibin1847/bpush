package com.github.guhaibin.netty.handler;

import com.github.guhaibin.api.MessageDispatcher;
import com.github.guhaibin.api.connection.Connection;
import com.github.guhaibin.api.protocol.Packet;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
@ChannelHandler.Sharable
public class ClientWebSocketHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger LOG = LoggerFactory.getLogger(ClientWebSocketHandler.class);
    private WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakerFuture;
    private MessageDispatcher dispatcher;
    private Connection connection;


    public ClientWebSocketHandler(WebSocketClientHandshaker handshaker,
                                  MessageDispatcher dispatcher,
                                  Connection connection){
        this.handshaker = handshaker;
        this.dispatcher = dispatcher;
        this.connection = connection;
    }

    public ChannelFuture handshakeFuture() {
        return handshakerFuture;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        handshakerFuture = ctx.newPromise();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("WebSocket Client disconnected!");
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch = ctx.channel();
        if (!handshaker.isHandshakeComplete()){
            handshaker.finishHandshake(ch, (FullHttpResponse) msg);
            LOG.info("WebSocket client connected");
            handshakerFuture.setSuccess();
            return;
        }
        if (msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;
            throw new IllegalStateException(
                    "Unexpected FullHttpResponse (getStatus=" + response.status() +
                            ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        }

        WebSocketFrame frame = (WebSocketFrame)msg;
        if (frame instanceof TextWebSocketFrame){

            Packet packet = Packet.fromFrame((TextWebSocketFrame)frame);
            dispatcher.receive(packet, connection);

        } else if (frame instanceof PongWebSocketFrame){

            LOG.info("pong！");

        } else if (frame instanceof CloseWebSocketFrame){

            LOG.warn("server send close frame");

            ch.close();
        }

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        handshaker.handshake(ctx.channel());
        LOG.info("connect to server, begin hand shaker");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOG.error("error happened", cause);
        if (!handshakerFuture.isDone()) {
            handshakerFuture.setFailure(cause);
        }
        ctx.close();
    }
}
