package com.github.guhaibin.api.spi.service;

import com.github.guhaibin.api.push.User;

import java.util.Set;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public interface DataService {

    void addUser(User user, String channelId, String tag);

    String findChannelId(User user);

    User findUser(String channelId);

    void removeUser(User user);

    Set<String> findChannelIdsByTag(String tag);

    void removeTag(String tag);

    boolean online(User user);

    void clearAll();
}
