package com.github.guhaibin.api.service;


import com.github.guhaibin.api.connection.Connection;
import com.github.guhaibin.api.push.MessageReceiver;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public interface Client  {
    void start();
    void connect();
    void close();
    void beforeReconnect();
    void reconnect();
    boolean isClosed();

    Connection getConnection();
    String getInfo();
    byte getStatus();
    MessageReceiver getReceiver();
}
