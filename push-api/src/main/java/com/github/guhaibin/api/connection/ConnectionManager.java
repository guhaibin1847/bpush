package com.github.guhaibin.api.connection;

import io.netty.channel.Channel;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public interface ConnectionManager {
    Connection get(Channel channel);
    Connection get(String channelId);
    void put(Connection connection);
    Connection remove(Channel channel);
    Connection remove(String channelId);
    void destroy();
    int count();
}
