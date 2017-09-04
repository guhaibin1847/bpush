package com.github.guhaibin.api;

import com.github.guhaibin.api.connection.Connection;
import com.github.guhaibin.api.protocol.Packet;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public interface PacketReceiver {
    void receive(Packet packet, Connection connection);
}
