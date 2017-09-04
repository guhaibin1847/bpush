package com.github.guhaibin.api.event;

import com.github.guhaibin.api.connection.Connection;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class ConnectionCloseEvent implements Event {
    private Connection connection;

    public ConnectionCloseEvent(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }
}
