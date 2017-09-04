package com.github.guhaibin.api.push;

import com.github.guhaibin.api.service.Client;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public interface Pusher {
    void push(SendMessage message, UserType userType, String ... toList);
    void gpush(SendMessage message, String tag);

    void syncPush(SendMessage message,
                  UserType userType,
                  String to,
                  PushCallback callback);

    PushResult syncPush(SendMessage message,
                        UserType userType,
                        String to);

    boolean isOnline(UserType userType, String userId);

    Client getClient();

    void start();
    void close();
    byte getStatus();
}
