package com.github.guhaibin.core.message;

import com.github.guhaibin.api.protocol.Command;
import com.github.guhaibin.api.protocol.Flags;
import com.github.guhaibin.api.protocol.Packet;
import com.github.guhaibin.api.push.Message;
import com.github.guhaibin.api.push.User;
import com.github.guhaibin.core.exception.UnsupportedPacketException;
import com.github.guhaibin.utils.Jsons;
import com.github.guhaibin.utils.common.IdGen;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class ReceivedMessage implements Message {

    private String sessionId;
    private User from;
    private String message;
    private boolean needAck;
    private Integer ackResult;

    public ReceivedMessage(){}

    public ReceivedMessage(String message, boolean needAck, String sessionId){
        this(message, needAck);
        this.sessionId = sessionId;
    }

    public ReceivedMessage(String message, boolean needAck){
        this.message = message;
        this.needAck = needAck;
        sessionId = IdGen.uuid();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isNeedAck() {
        return needAck;
    }

    public void setNeedAck(boolean needAck) {
        this.needAck = needAck;
    }

    public Integer getAckResult() {
        return ackResult;
    }

    public void setAckResult(Integer ackResult) {
        this.ackResult = ackResult;
    }

    public String getSessionId() {
        return sessionId;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    @Override
    public Packet to() {
        //throw new UnsupportedPacketException("received message can not been packet");
        Packet packet = new Packet(Command.MESSAGE);
        packet.setSessionId(sessionId);
        packet.setAttribute("message", message);
        packet.setAttribute("from", Jsons.toJson(from));
        if (needAck){
            packet.addFlag(Flags.ACK);
        }
        return packet;
    }

    private Packet f;
    @Override
    public void from(Packet packet) {
        this.message = packet.getAttribute("message");
        this.sessionId = packet.getSessionId();
        if (packet.hasFlag(Flags.ACK)){
            this.needAck = true;
        }else {
            this.needAck = false;
        }
        this.ackResult = packet.getAttribute("ackResult");
        if (packet.getAttribute("from") != null) {
            this.from = Jsons.fromJson(packet.getAttribute("from").toString(), User.class);
        }
        this.f = packet;
    }

    @Override
    public Packet ack(int result) {
        Packet p = f.ack(result);
        p.setAttribute("from", from);
        return p;
    }

    @Override
    public String toString() {
        return "ReceivedMessage{" +
                "sessionId='" + sessionId + '\'' +
                ", from='" + from + '\'' +
                ", message='" + message + '\'' +
                ", needAck=" + needAck +
                ", ackResult=" + ackResult +
                ", f=" + f +
                '}';
    }
}
