package com.github.guhaibin.api.push;

import com.github.guhaibin.api.protocol.Packet;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public interface Message {
    Packet to();
    void from(Packet packet);
    Packet ack(int result);
}
