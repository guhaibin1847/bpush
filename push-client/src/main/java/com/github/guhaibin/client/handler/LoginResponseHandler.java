package com.github.guhaibin.client.handler;

import com.github.guhaibin.api.MessageHandler;
import com.github.guhaibin.api.connection.Connection;
import com.github.guhaibin.core.message.LoginMessage;
import com.github.guhaibin.utils.concurrency.StatusCountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class LoginResponseHandler implements MessageHandler<LoginMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(LoginResponseHandler.class);

    @Override
    public void handle(LoginMessage message, Connection connection) {
        String sessionId = message.getSessionId();
        boolean success = message.isSuccess();

        LOG.info("login response. message is '{}'", message);

        StatusCountDownLatch.countDown(sessionId, success);
    }
}
