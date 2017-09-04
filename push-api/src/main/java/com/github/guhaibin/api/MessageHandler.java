package com.github.guhaibin.api;

import com.github.guhaibin.api.connection.Connection;
import com.github.guhaibin.api.protocol.Packet;
import com.github.guhaibin.api.push.Message;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public interface MessageHandler<T extends Message> {
    void handle(T message, Connection connection);
}
