package com.github.guhaibin.client.handler;

import com.github.guhaibin.api.MessageHandler;
import com.github.guhaibin.api.connection.Connection;
import com.github.guhaibin.api.push.MessageReceiver;
import com.github.guhaibin.api.push.PushResult;
import com.github.guhaibin.api.push.SendMessage;
import com.github.guhaibin.core.message.PushMessage;
import com.github.guhaibin.core.message.ReceivedMessage;
import com.github.guhaibin.utils.Jsons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class MessageReceiveHandler implements MessageHandler<ReceivedMessage> {

    private static final Logger LOG = LoggerFactory.getLogger(MessageReceiveHandler.class);

    private final MessageReceiver receiver;
    private Executor executor;

    public MessageReceiveHandler(MessageReceiver receiver, Executor executor){
        this.receiver = receiver;
        this.executor = executor;
    }

    @Override
    public void handle(ReceivedMessage message, Connection connection) {
        String msg = message.getMessage();
        executor.execute(
                () -> {
                    int result = receiver.onReceive(Jsons.fromJson(msg, SendMessage.class));
                    if (message.isNeedAck()){
                        LOG.info("message need ack, so send ack. message is '{}'", message);

                        connection.send(message.ack(result));
                    }
                }
        );
    }
}
