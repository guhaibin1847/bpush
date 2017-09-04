package com.github.guhaibin.server.handler;

import com.github.guhaibin.api.MessageHandler;
import com.github.guhaibin.api.connection.Connection;
import com.github.guhaibin.api.connection.ConnectionManager;
import com.github.guhaibin.api.push.User;
import com.github.guhaibin.api.spi.common.CacheManager;
import com.github.guhaibin.api.spi.service.DataService;
import com.github.guhaibin.core.message.AckMessage;
import com.github.guhaibin.core.message.PushMessage;
import com.github.guhaibin.core.message.ReceivedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class AckMessageHandler implements MessageHandler<AckMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(AckMessageHandler.class);

    private ConnectionManager connectionManager;
    private DataService dataService;

    public AckMessageHandler(ConnectionManager connectionManager,
                             DataService dataService){
        this.connectionManager = connectionManager;
        this.dataService = dataService;
    }

    @Override
    public void handle(AckMessage message, Connection connection) {
        LOG.trace("receive ack message, message is '{}'", message);

        User from = message.getFrom();
        String channelId = dataService.findChannelId(from);
        Connection conn = connectionManager.get(channelId);
        if (conn != null){
            conn.send(message.to());
        }else {
            LOG.warn("endpoint can not been found when send ack, message is '{}'", message);
        }
    }
}
