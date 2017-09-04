package com.github.guhaibin.server.handler;

import com.github.guhaibin.api.Config;
import com.github.guhaibin.api.MessageHandler;
import com.github.guhaibin.api.connection.Connection;
import com.github.guhaibin.core.message.HeartbeatMessage;
import com.github.guhaibin.server.Logs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class HeartbeatHandler implements MessageHandler<HeartbeatMessage> {

    @Override
    public void handle(HeartbeatMessage message, Connection connection) {
        if (!Config.dev) {
            Logs.HEART_BEAT.trace("heart beat from '{}'", connection);
        }
        connection.updateLastReadTime();
    }
}
