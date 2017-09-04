package com.github.guhaibin.core.message;

import com.github.guhaibin.api.protocol.Packet;
import com.github.guhaibin.api.push.Message;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class HeartbeatMessage implements Message {
    @Override
    public Packet to() {
        return Packet.HEART_BEAT_PACKET;
    }

    @Override
    public void from(Packet packet) {

    }

    @Override
    public Packet ack(int ackResult) {
        return Packet.HEART_BEAT_PACKET;
    }
}
