package com.github.guhaibin.api.push;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public interface MessageReceiver {
    void onNew();
    void onConnected();
    int onReceive(SendMessage message);
    void onInactive();
}
