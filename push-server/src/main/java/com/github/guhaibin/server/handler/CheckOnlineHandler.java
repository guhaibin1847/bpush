package com.github.guhaibin.server.handler;

import com.github.guhaibin.api.MessageHandler;
import com.github.guhaibin.api.connection.Connection;
import com.github.guhaibin.api.push.PushResult;
import com.github.guhaibin.api.push.User;
import com.github.guhaibin.api.spi.service.DataService;
import com.github.guhaibin.core.message.CheckOnlineMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class CheckOnlineHandler implements MessageHandler<CheckOnlineMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(CheckOnlineHandler.class);

    private DataService dataService;

    public CheckOnlineHandler(DataService dataService){
        this.dataService = dataService;
    }

    @Override
    public void handle(CheckOnlineMessage message, Connection connection) {
        User user = message.getUser();

        LOG.info("check if '{}' is online", user);

        if (dataService.online(user)){
            connection.send(message.ack(PushResult.SUCCESS));
        }else {
            connection.send(message.ack(PushResult.OFF_LINE));
        }
    }
}
