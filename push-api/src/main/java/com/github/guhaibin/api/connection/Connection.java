package com.github.guhaibin.api.connection;

import com.github.guhaibin.api.protocol.Packet;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;


/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public interface Connection {

    byte NEW = 1;
    byte CONNECTED = 2;
    byte AUTHORIZED = 3;
    byte DISCONNECTED = 4;

    void init(Channel channel);

    SessionContext getSessionContext();

    void setSessionContext(SessionContext sessionContext);

    ChannelFuture send(Packet packet);

    ChannelFuture send(Packet packet, ChannelFutureListener listener);

    String getId();

    ChannelFuture close();

    boolean isConnected();

    boolean isAuthorized();

    void markAuthorized();

    boolean isReadTimeOut();

    boolean isWriteTimeOut();

    void updateLastReadTime();

    void updateLastWriteTime();

    Channel getChannel();

    byte getStatus();


}
