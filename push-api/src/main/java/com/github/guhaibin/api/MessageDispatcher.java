package com.github.guhaibin.api;

import com.github.guhaibin.api.connection.Connection;
import com.github.guhaibin.api.protocol.Command;
import com.github.guhaibin.api.protocol.Packet;
import com.github.guhaibin.api.push.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public final class MessageDispatcher implements PacketReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(MessageDispatcher.class);

    private Executor executor;
    private Map<Byte, MessageHandler> handlers = new ConcurrentHashMap<>();
    private Map<String, Class<Message>> typeCache = new ConcurrentHashMap<>();

    public MessageDispatcher(Executor executor){
        this.executor = executor;
    }

    public void register(Command command, MessageHandler handler){
        handlers.put(command.cmd, handler);
    }

    @Override
    public void receive(Packet packet, Connection connection) {
        executor.execute(() -> receive0(packet, connection));
    }

    private void receive0(Packet packet, Connection connection){

        LOG.trace("dispatch packet. packet is '{}'", packet);

        MessageHandler handler = handlers.get(packet.getCmd());
        if (handler != null){
            try{
                Class<Message> clazz = getMessageType(handler);

                if (clazz == null){
                    LOG.error("can not find message type from handler {}, packet is '{}'",
                            handler, packet);
                    return;
                }

                Message message = clazz.newInstance();
                message.from(packet);
                handler.handle(message, connection);
            }catch (Throwable t){
                LOG.error("handle packet error. packet is '{}', connection is '{}'", packet, connection, t);
            }
        }else {
            LOG.warn("can not find handle for packet '{}'", packet);
        }
    }

    private Class<Message> getMessageType(MessageHandler handler){
        Class clazz = handler.getClass();
        String name = clazz.getName();
        if (typeCache.containsKey(name)){
            return typeCache.get(name);
        }else {
            while (clazz != Object.class) {
                Type[] types = clazz.getGenericInterfaces();
                for (Type type : types) {
                    if (type instanceof ParameterizedType) {
                        ParameterizedType ptype = ((ParameterizedType) type);
                        Type rawType = ptype.getRawType();
                        if (rawType.getTypeName().equals(MessageHandler.class.getName())) {
                            Type t = ptype.getActualTypeArguments()[0];
                            Class<Message> ret = (Class<Message>) t;
                            typeCache.put(name, ret);
                            return ret;
                        }
                    }
                }
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

}
