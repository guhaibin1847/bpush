package com.github.guhaibin.core.message;

import com.github.guhaibin.api.protocol.Command;
import com.github.guhaibin.api.protocol.Flags;
import com.github.guhaibin.api.protocol.Packet;
import com.github.guhaibin.api.push.Message;
import com.github.guhaibin.api.push.User;
import com.github.guhaibin.api.push.UserType;
import com.github.guhaibin.api.spi.common.Json;
import com.github.guhaibin.utils.Jsons;
import com.github.guhaibin.utils.common.IdGen;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class CheckOnlineMessage implements Message {

    private String sessionId;
    private User user;

    public CheckOnlineMessage(){
        this.sessionId = IdGen.uuid();
    }


    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public Packet to() {
        Packet p = new Packet(Command.CHECK_ONLINE);
        p.setSessionId(sessionId);
        p.addFlag(Flags.ACK);
        p.setAttribute("user", Jsons.toJson(user));
        return p;
    }

    @Override
    public void from(Packet packet) {
        sessionId = packet.getSessionId();
        String userJson = packet.getAttribute("user");
        user = Jsons.fromJson(userJson, User.class);
    }

    @Override
    public Packet ack(int result) {
        return Packet.ack(sessionId, result);
    }
}
