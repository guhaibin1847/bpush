package com.github.guhaibin.netty.connection;

import com.github.guhaibin.api.connection.Connection;
import com.github.guhaibin.api.connection.ConnectionManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class NettyConnectionManager implements ConnectionManager{

    private static final Logger LOG = LoggerFactory.getLogger(NettyConnectionManager.class);

    private final Map<ChannelId, Connection> connections = new ConcurrentHashMap<>();


    @Override
    public Connection get(Channel channel) {
        return connections.get(channel.id());
    }

    @Override
    public Connection get(String channelId) {
        return null;
    }

    @Override
    public void put(Connection connection) {
        connections.put(connection.getChannel().id(), connection);
    }

    @Override
    public Connection remove(Channel channel) {
        return connections.remove(channel.id());
    }

    @Override
    public Connection remove(String channelId) {
        return null;
    }

    @Override
    public void destroy() {
        connections.values().forEach(Connection::close);
        connections.clear();
    }

    @Override
    public int count() {
        return connections.size();
    }
}
