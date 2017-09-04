package com.github.guhaibin.core.message;

import com.github.guhaibin.api.protocol.Command;
import com.github.guhaibin.api.protocol.Flags;
import com.github.guhaibin.api.protocol.Packet;
import com.github.guhaibin.api.push.Message;
import com.github.guhaibin.api.push.UserType;
import com.github.guhaibin.utils.common.IdGen;

import java.util.List;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class PushMessage extends BaseMessage implements Message {

    private String sessionId;
    private UserType userType;
    private List<String> toList;
    private String message;
    private boolean needAck;

    public PushMessage(){}

    public PushMessage(String message){
        this.message = message;
        this.sessionId = IdGen.uuid();
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

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public List<String> getToList() {
        return toList;
    }

    public void setToList(List<String> toList) {
        this.toList = toList;
    }

    public String getSessionId() {
        return sessionId;
    }

    @Override
    public Packet to() {
        Packet packet = new Packet(Command.MESSAGE);
        packet.setSessionId(sessionId);
        packet.setAttribute("message", message);
        packet.setAttribute("userType", userType.typeVal);
        packet.setAttribute("toList", toList);
        if (needAck){
            packet.addFlag(Flags.ACK);
        }
        return packet;
    }

    @Override
    public void from(Packet packet) {
        this.sessionId = packet.getSessionId();
        this.message = packet.getAttribute("message");
        int val = packet.getAttribute("userType");
        this.userType = UserType.to(val);
        this.toList = packet.getAttribute("toList");
        if (packet.hasFlag(Flags.ACK)){
            this.needAck = true;
        }else {
            this.needAck = false;
        }
    }

    @Override
    public Packet ack(int ackResult) {
        return to().ack(ackResult);
    }


    @Override
    public String toString() {
        return "PushMessage{" +
                "sessionId='" + sessionId + '\'' +
                ", message='" + message + '\'' +
                ", needAck=" + needAck +
                '}';
    }
}
