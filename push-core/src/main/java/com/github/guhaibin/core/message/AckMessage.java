package com.github.guhaibin.core.message;

import com.github.guhaibin.api.protocol.Command;
import com.github.guhaibin.api.protocol.Packet;
import com.github.guhaibin.api.push.Message;
import com.github.guhaibin.api.push.PushResult;
import com.github.guhaibin.api.push.User;
import com.github.guhaibin.utils.Jsons;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class AckMessage implements Message{

    private String sessionId;
    private User from;
    private int ackResult;

    public int getAckResult() {
        return ackResult;
    }

    public boolean isSuccess(){
        return ackResult == PushResult.SUCCESS;
    }

    @Override
    public Packet to() {
        return p;
    }

    private Packet p;
    @Override
    public void from(Packet packet) {
        Object o = packet.getAttribute("ackResult");
        if (o != null){
            ackResult = (int)o;
        }else {
            ackResult = PushResult.FAILELD;
        }
        sessionId = packet.getSessionId();
        if (packet.getAttribute("from") != null) {
            this.from = Jsons.fromJson(packet.getAttribute("from").toString(), User.class);
        }
        this.p = packet;
    }

    @Override
    public Packet ack(int result) {
        return p;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public void setAckResult(int ackResult) {
        this.ackResult = ackResult;
    }

    @Override
    public String toString() {
        return "AckMessage{" +
                "ackResult=" + ackResult +
                '}';
    }
}
