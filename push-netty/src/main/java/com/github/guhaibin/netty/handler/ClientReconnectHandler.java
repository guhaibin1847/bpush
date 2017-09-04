package com.github.guhaibin.netty.handler;

import com.github.guhaibin.api.Config;
import com.github.guhaibin.api.connection.Connection;
import com.github.guhaibin.api.push.MessageReceiver;
import com.github.guhaibin.api.service.Client;
import com.github.guhaibin.api.service.Listener;
import com.github.guhaibin.core.exception.ConnectFailedException;
import com.github.guhaibin.utils.mail.Email;
import com.github.guhaibin.utils.mail.MailSender;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class ClientReconnectHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(ClientReconnectHandler.class);
    private static final int MAX_RETRY_COUNT = Config.WebSocketConf.maxRetryCount;

    private Client client;
    private int retryCount = 0;

    public ClientReconnectHandler(Client client){
        this.client = client;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        MessageReceiver receiver = client.getReceiver();
        receiver.onInactive();

        Connection connection = client.getConnection();
        connection.close();
        if (!client.isClosed()) {

            LOG.warn("client lost connect to server, try to reconnect to server");

            client.beforeReconnect();
            ctx.channel().eventLoop().schedule(() -> reconnect(ctx.channel(), client),
                    Config.WebSocketConf.clientReconnectInterval, TimeUnit.SECONDS);
        }
    }


    private void reconnect(Channel channel, Client client){
        try {
            retryCount++;
            client.reconnect();
        }catch (ConnectFailedException e){
            LOG.error("reconnect error, retry count is '{}'", retryCount, e);

            if (retryCount < MAX_RETRY_COUNT) {
                channel.eventLoop().schedule(() -> {
                    reconnect(channel, client);
                }, Config.WebSocketConf.clientReconnectInterval * retryCount, TimeUnit.SECONDS);
            } else {
                LOG.warn("reconnect failed!!!");
                // 网都连不上，发邮件有啥用。。。
                Email email = Email.me();
                email.setCc(Config.Alert.toList);
                email.setSubject("client connect server error, retry failed.");
                email.setContent("client info is " + client.getInfo());
                channel.eventLoop().execute(() -> MailSender.send(email));
            }
        }
        retryCount = 0;
    }

}
