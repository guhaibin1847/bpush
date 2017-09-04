package com.github.guhaibin.server.handler;

import com.github.guhaibin.api.MessageHandler;
import com.github.guhaibin.api.connection.Connection;
import com.github.guhaibin.api.connection.ConnectionManager;
import com.github.guhaibin.api.push.User;
import com.github.guhaibin.api.push.UserType;
import com.github.guhaibin.api.spi.service.DataService;
import com.github.guhaibin.core.message.PushMessage;
import com.github.guhaibin.core.message.ReceivedMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class PushMessageHandler implements MessageHandler<PushMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(PushMessageHandler.class);
    private ConnectionManager connectionManager;
    private DataService dataService;

    public PushMessageHandler(ConnectionManager connectionManager,
                              DataService dataService){
        this.connectionManager = connectionManager;
        this.dataService = dataService;
    }

    @Override
    public void handle(PushMessage message, Connection connection) {

        LOG.trace("receive push message, message is '{}'", message);

        UserType userType = message.getUserType();
        if (userType == UserType.TAG ){
            handleTag(message, connection);
        }else if (userType == UserType.CLIENT_USER){
            handleUser(message, connection);
        }else {
            LOG.warn("unknown user type -> '{}'", userType);
        }

        connection.updateLastReadTime();
    }


    private void handleUser(PushMessage message, Connection connection){
        UserType userType = message.getUserType();
        boolean needAck = message.isNeedAck();
        List<String> toList = message.getToList();

        if (needAck && toList.size() > 1){
            LOG.warn("!!! message need ack can not use more than one endpoint, send with no ack");
            needAck = false;
        }
        ReceivedMessage receivedMessage = new ReceivedMessage(message.getMessage(), needAck, message.getSessionId());
        User from = dataService.findUser(connection.getId());
        receivedMessage.setFrom(from);
        for (String to : toList){
            User user = new User(userType, to);
            String channelId = dataService.findChannelId(user);
            if (StringUtils.isBlank(channelId)){
                LOG.warn("to endpoint has not been register to server, message is '{}', to is '{}'",
                        message, to);
                // if keep, save to redis todo
                continue;
            }
            Connection conn = connectionManager.get(channelId);
            if (conn == null || !conn.isConnected()){
                LOG.warn("can not find connection with '{}'", channelId);
                LOG.warn("endpoint connection has been closed, message is '{}', to is '{}'",
                        message, to);
                // if keep, save to redis todo
                continue;
            }

            conn.send(receivedMessage.to());
        }

    }

    private void handleTag(PushMessage message, Connection connection){
        String tag = message.getToList().get(0);
        if (StringUtils.isBlank(tag)){
            LOG.warn("tag is empty when send to tag, message is '{}'", message);
            return;
        }

        ReceivedMessage receivedMessage = new ReceivedMessage(message.getMessage(), false);
        User from = dataService.findUser(connection.getId());
        receivedMessage.setFrom(from);

        Set<String> channelIds = dataService.findChannelIdsByTag(tag);
        if (channelIds != null && !channelIds.isEmpty()){
            for (String channelId : channelIds){
                Connection conn = connectionManager.get(channelId);
                if (conn == null){
                    LOG.warn("can not find connection with '{}'", channelId);
                    // if keep, save to redis todo
                }else {
                    conn.send(receivedMessage.to());
                }
            }
        }else {
            LOG.warn("tag dose not relate any user, tag is '{}'", tag);
        }
    }




}
