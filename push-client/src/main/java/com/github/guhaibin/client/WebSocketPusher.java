package com.github.guhaibin.client;

import com.github.guhaibin.api.Config;
import com.github.guhaibin.api.push.*;
import com.github.guhaibin.api.service.Client;
import com.github.guhaibin.core.exception.UnsupportUserTypeException;
import com.github.guhaibin.core.message.CheckOnlineMessage;
import com.github.guhaibin.core.message.PushMessage;
import com.github.guhaibin.utils.Jsons;
import com.github.guhaibin.utils.concurrency.StatusCountDownLatch;

import java.util.Arrays;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class WebSocketPusher implements Pusher {

    private final Client client;

    public WebSocketPusher(MessageReceiver receiver){
        this(receiver, Config.ClientConf.userType, Config.ClientConf.username);
    }

    public WebSocketPusher(MessageReceiver receiver,
                           UserType userType,
                           String username){
        User user = new User(userType, username);
        this.client = new WebSocketClient(receiver, user);
    }


    public void start(){
        client.start();
    }

    public void close(){
        client.close();
    }

    @Override
    public byte getStatus() {
        return this.client.getStatus();
    }

    @Override
    public void push(SendMessage message, UserType userType, String... toList) {
        PushMessage pushMessage = new PushMessage(Jsons.toJson(message));
        pushMessage.setUserType(userType);
        pushMessage.setToList(Arrays.asList(toList));
        client.getConnection().send(pushMessage.to());
    }

    @Override
    public void gpush(SendMessage message, String tag) {
        PushMessage pushMessage = new PushMessage(Jsons.toJson(message));
        pushMessage.setUserType(UserType.TAG);
        pushMessage.setToList(Arrays.asList(tag));
        client.getConnection().send(pushMessage.to());
    }

    @Override
    public void syncPush(SendMessage message, UserType userType, String to, PushCallback callback) {

        PushResult pushResult = syncPush(message, userType, to);
        if (callback != null) {
            callback.onResult(pushResult);
        }
    }

    @Override
    public PushResult syncPush(SendMessage message, UserType userType, String to) {
        if (userType == UserType.TAG){
            throw new UnsupportUserTypeException("sync push do not support tag user type");
        }

        PushMessage pushMessage = new PushMessage(Jsons.toJson(message));
        pushMessage.setUserType(userType);
        pushMessage.setToList(Arrays.asList(to));
        pushMessage.setNeedAck(true);
        String sessionId = pushMessage.getSessionId();
        client.getConnection().send(pushMessage.to());
        StatusCountDownLatch latch = new StatusCountDownLatch(sessionId, 1);
        boolean success = latch.await(Config.ClientConf.ackTimeOut);
        int result = PushResult.FAILELD;
        if (success){
            result = latch.getResult();
        }else {
            result = PushResult.TIME_OUT;
        }
        return new PushResult(result, to);
    }

    @Override
    public boolean isOnline(UserType userType, String username) {
        CheckOnlineMessage message = new CheckOnlineMessage();
        message.setUser(new User(userType, username));
        String sessionId = message.getSessionId();
        client.getConnection().send(message.to());
        StatusCountDownLatch latch = new StatusCountDownLatch(sessionId, 1);
        boolean success = latch.await(Config.ClientConf.ackTimeOut);

        return success && latch.getResult() == PushResult.SUCCESS;
    }

    public Client getClient(){
        return this.client;
    }
}
