package com.github.guhaibin.api.event;

import com.github.guhaibin.api.connection.Connection;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class ConnectionConnectEvent implements Event {
    private Connection connection;

    public ConnectionConnectEvent(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }
}
