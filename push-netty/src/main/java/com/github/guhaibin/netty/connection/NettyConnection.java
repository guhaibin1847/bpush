package com.github.guhaibin.netty.connection;

import com.github.guhaibin.api.Config;
import com.github.guhaibin.api.connection.Connection;
import com.github.guhaibin.api.connection.SessionContext;
import com.github.guhaibin.api.protocol.Packet;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class NettyConnection implements Connection, ChannelFutureListener{

    private static final Logger LOG = LoggerFactory.getLogger(NettyConnection.class);

    private SessionContext sessionContext;
    private volatile byte status = NEW;
    private Channel channel;
    private long lastReadTime;
    private long lastWriteTime;

    public NettyConnection(){
    }

    @Override
    public void init(Channel channel) {
        this.channel = channel;
        this.lastReadTime = System.currentTimeMillis();
        this.lastWriteTime = System.currentTimeMillis();
        status = CONNECTED;
    }

    @Override
    public SessionContext getSessionContext() {
        return this.sessionContext;
    }

    @Override
    public void setSessionContext(SessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    @Override
    public ChannelFuture send(Packet packet) {
        return send(packet, new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    LOG.trace("send packet successfully, packet is '{}'", packet);
                }else {
                    LOG.error("send packet error, packet is '{}'", packet, future.cause());
                }
            }
        });
    }

    @Override
    public ChannelFuture send(Packet packet, ChannelFutureListener listener) {
        if (channel.isActive()){
            ChannelFuture future = channel.writeAndFlush(packet.toFrame()).addListener(this);
            if (listener != null){
                future.addListener(listener);
            }
            return future;
        }else {
            return channel.close();
        }
    }

    @Override
    public String getId() {
        return this.channel.id().asLongText();
    }

    @Override
    public ChannelFuture close() {
        if (status == DISCONNECTED)
            return null;
        status = DISCONNECTED;
        return channel.close();
    }

    @Override
    public boolean isConnected() {
        return status == CONNECTED;
    }

    @Override
    public boolean isAuthorized() {
        return status == AUTHORIZED;
    }

    @Override
    public void markAuthorized() {
        this.status = AUTHORIZED;
    }

    @Override
    public boolean isReadTimeOut() {
        return (System.currentTimeMillis() - lastReadTime) / 1000 > Config.WebSocketConf.timeout;
    }

    @Override
    public boolean isWriteTimeOut() {
        return (System.currentTimeMillis() - lastWriteTime) / 1000 > Config.WebSocketConf.timeout;
    }

    @Override
    public void updateLastReadTime() {
        this.lastReadTime = System.currentTimeMillis();
    }

    @Override
    public void updateLastWriteTime() {
        this.lastWriteTime = System.currentTimeMillis();
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public byte getStatus() {
        return status;
    }

    @Override
    public void operationComplete(ChannelFuture channelFuture) throws Exception {
        if (channelFuture.isSuccess()){
            this.lastWriteTime = System.currentTimeMillis();
        }else {
            LOG.error("connection send data error, connection is '{}'", this, channelFuture.cause());
        }
    }

    @Override
    public String toString() {
        return "NettyConnection{" +
                "sessionContext=" + sessionContext +
                ", status=" + status +
                ", channel=" + channel +
                ", lastReadTime=" + lastReadTime +
                ", lastWriteTime=" + lastWriteTime +
                '}';
    }
}
