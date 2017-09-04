package com.github.guhaibin.core.message;

import com.github.guhaibin.api.protocol.Command;
import com.github.guhaibin.api.protocol.Flags;
import com.github.guhaibin.api.protocol.Packet;
import com.github.guhaibin.api.push.Message;
import com.github.guhaibin.api.push.PushResult;
import com.github.guhaibin.api.push.UserType;
import com.github.guhaibin.core.exception.ErrorPacketException;
import com.github.guhaibin.utils.common.IdGen;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class LoginMessage implements Message {

    private String sessionId;
    private UserType userType;
    private String username;
    private String password;
    private String tag;
    private int ackResult;

    public LoginMessage(){
        this.sessionId = IdGen.uuid();
    }

    public LoginMessage(UserType userType, String username, String password) {
        this();
        this.userType = userType;
        this.username = username;
        this.password = password;
    }

    public String getSessionId() {
        return sessionId;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public Packet to() {
        Packet packet = new Packet(Command.LOGIN);
        packet.setSessionId(sessionId);
        packet.addFlag(Flags.ACK);
        packet.setAttribute("username", username);
        packet.setAttribute("password", password);
        packet.setAttribute("userType", userType.typeVal);
        packet.setAttribute("tag", tag);
        return packet;
    }

    @Override
    public void from(Packet packet) {
        byte cmd = packet.getCmd();
        if (cmd != Command.LOGIN.cmd){
            throw new ErrorPacketException("need login packet");
        }
        this.sessionId = packet.getSessionId();
        this.username = packet.getAttribute("username");
        this.password = packet.getAttribute("password");
        int val = packet.getAttribute("userType");
        this.userType = UserType.to(val);
        this.tag = packet.getAttribute("tag");
        Object o = packet.getAttribute("ackResult");
        if (o != null){
            ackResult = (int)o;
        }else {
            ackResult = PushResult.FAILELD;
        }
    }

    public Packet ack(int ackResult){
        Packet packet = to().response(ackResult);
        packet.setAttribute("userType", userType.typeVal);
        return packet;
    }

    public boolean isSuccess(){
        return ackResult == PushResult.SUCCESS;
    }

    @Override
    public String toString() {
        return "\nLoginMessage{" +
                "sessionId='" + sessionId + '\'' +
                ", userType=" + userType +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
