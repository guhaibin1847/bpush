package com.github.guhaibin.api.protocol;

import com.github.guhaibin.api.push.Message;
import com.github.guhaibin.api.spi.common.Json;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class Packet {

    private static final Logger LOG = LoggerFactory.getLogger(Packet.class);

    public static final Packet HEART_BEAT_PACKET = new Packet(Command.HEART_BEAT);

    private byte cmd;
    private String sessionId;
    private byte flags = 0;

    private Map<String, Object> body;

    public Packet(){}

    public Packet(byte cmd){
        this.cmd = cmd;
    }

    public Packet(Command command){
        this(command.cmd);
    }

    public Packet(byte cmd, String sessionId){
        this.cmd = cmd;
        this.sessionId = sessionId;
    }

    public Packet(Command command, String sessionId){
        this(command.cmd, sessionId);
    }

    public Packet response(Command command, int result){
        Packet packet = new Packet(command, sessionId);
        packet.setAttribute("ackResult", result);
        return packet;
    }

    public Packet response(int result){
        Packet packet = new Packet(this.cmd, sessionId);
        packet.setAttribute("ackResult", result);
        return packet;
    }

    public Packet ack(int result){
        Packet packet = new Packet(Command.ACK, sessionId);
        packet.setAttribute("ackResult", result);
        return packet;
    }

    public static Packet ack(String sessionId, int result){
        Packet packet = new Packet(Command.ACK, sessionId);
        packet.setAttribute("ackResult", result);
        return packet;
    }

    public static Packet fromFrame(TextWebSocketFrame frame){
        String json = frame.text();
        LOG.trace("frame text is '{}'", json);
        return Json.JSON.fromJson(json, Packet.class);
    }

    public TextWebSocketFrame toFrame(){
        byte[] json = Json.JSON.toJson(this).getBytes(Charset.forName("utf8"));
        return new TextWebSocketFrame(Unpooled.wrappedBuffer(json));
    }


    public <T> T getAttribute(String key){
        if (body != null){
            return (T)body.get(key);
        }
        return null;
    }

    public void setAttribute(String key, Object value){
        if (body == null){
            body = new ConcurrentHashMap<>();
        }
        if (key != null && value != null) {
            body.put(key, value);
        }
    }

    public void addFlag(Flags flag) {
        this.flags |= flag.flag;
    }

    public boolean hasFlag(Flags flag) {
        return (flags & flag.flag) != 0;
    }

    public byte getCmd() {
        return cmd;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setCmd(byte cmd) {
        this.cmd = cmd;
    }

    public byte getFlags() {
        return flags;
    }

    public void setFlags(byte flags) {
        this.flags = flags;
    }

    public Map<String, Object> getBody() {
        return body;
    }

    public void setBody(Map<String, Object> body) {
        this.body = body;
    }

    /*@Override
    public Packet to() {
        return this;
    }

    @Override
    public void from(Packet packet) {
        this.cmd = packet.cmd;
        this.body = packet.body;
        this.flags = packet.flags;
    }*/

    public <T extends Message> T to(Class<T> clazz) {
        try {
            T instance = clazz.newInstance();
            Field[] fields = instance.getClass().getDeclaredFields();
            for (Field f : fields){
                f.setAccessible(true);
                String name = f.getName();
                Object o = getAttribute(name);
                if (o != null){
                    f.set(instance, o);
                }
            }
            return instance;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return "Packet{" +
                "cmd=" + cmd +
                ", sessionId='" + sessionId + '\'' +
                ", flags=" + flags +
                ", body=" + body +
                '}';
    }


}
