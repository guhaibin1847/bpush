package com.github.guhaibin.client.handler;

import com.github.guhaibin.api.MessageHandler;
import com.github.guhaibin.api.connection.Connection;
import com.github.guhaibin.core.message.ReceivedMessage;
import com.github.guhaibin.utils.concurrency.StatusCountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class AckMessageHandler implements MessageHandler<ReceivedMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(AckMessageHandler.class);

    @Override
    public void handle(ReceivedMessage message, Connection connection) {
        LOG.info("receive ack message, message is '{}'", message);

        String sessionId = message.getSessionId();
        int result = message.getAckResult();

        StatusCountDownLatch.countDown(sessionId, result);
    }
}
