package com.github.guhaibin.netty.connection;

import com.github.guhaibin.api.Config;
import com.github.guhaibin.api.connection.Connection;
import com.github.guhaibin.api.connection.ConnectionManager;
import com.github.guhaibin.api.push.User;
import com.github.guhaibin.api.spi.service.DataService;
import com.github.guhaibin.utils.thread.NamedThreadFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class ServerConnectionManager implements ConnectionManager {

    private static final Logger LOG = LoggerFactory.getLogger(ServerConnectionManager.class);

    private DataService dataService;
    private final ConcurrentMap<String, ConnectionHolder> connections =
                                new ConcurrentHashMap<>();
    private final ConnectionHolder DEFAULT = new SimpleConnectionHolder(null);
    private final boolean heartbeatCheck;
    private final ConnectionHolderFactory holderFactory;
    private HashedWheelTimer timer;

    public ServerConnectionManager(boolean heartbeatCheck, DataService dataService) {
        this.heartbeatCheck = heartbeatCheck;
        this.holderFactory = heartbeatCheck ? HeartbeatCheckTask::new : SimpleConnectionHolder::new;

        if (this.heartbeatCheck) {
            long tickDuration = 1L;
            int ticksPerWheel = (int) (Config.WebSocketConf.timeout  / tickDuration);
            this.timer = new HashedWheelTimer(
                    new NamedThreadFactory("heartbeat-checker"),
                    tickDuration, TimeUnit.SECONDS, ticksPerWheel
            );
        }
        this.dataService = dataService;
    }

    @Override
    public void destroy() {
        if (timer != null) {
            timer.stop();
        }
        connections.values().forEach(ConnectionHolder::close);
        connections.clear();
    }

    @Override
    public Connection get(Channel channel) {
        return connections.getOrDefault(channel.id().asLongText(), DEFAULT).get();
    }

    @Override
    public Connection get(String channelId) {
        return connections.getOrDefault(channelId, DEFAULT).get();
    }

    @Override
    public void put(Connection connection) {
        String id = connection.getChannel().id().asLongText();
        connections.putIfAbsent(id, holderFactory.create(connection));
    }

    @Override
    public Connection remove(Channel channel) {
        String channelId = channel.id().asLongText();
        User user = dataService.findUser(channelId);
        if (user != null){
            dataService.removeUser(user);
        }
        return remove(channelId);
    }

    @Override
    public Connection remove(String channelId) {
        ConnectionHolder holder = connections.remove(channelId);
        if (holder != null) {
            Connection connection = holder.get();
            holder.close();
            return connection;
        }
        return null;
    }

    @Override
    public int count() {
        return connections.size();
    }

    public boolean getHeartbeatCheck(){
        return this.heartbeatCheck;
    }

    private interface ConnectionHolder {
        Connection get();

        void close();
    }

    private static class SimpleConnectionHolder implements ConnectionHolder {
        private final Connection connection;

        private SimpleConnectionHolder(Connection connection) {
            this.connection = connection;
        }

        @Override
        public Connection get() {
            return connection;
        }

        @Override
        public void close() {
            if (connection != null) {
                connection.close();
            }
        }
    }


    private class HeartbeatCheckTask implements ConnectionHolder, TimerTask {

        private byte timeoutTimes = 0;
        private Connection connection;

        private HeartbeatCheckTask(Connection connection) {
            this.connection = connection;
            this.startTimeout();
        }

        void startTimeout() {
            Connection connection = this.connection;

            if (connection != null && connection.isConnected()) {
                int timeout = Config.WebSocketConf.timeout;
                timer.newTimeout(this, timeout, TimeUnit.SECONDS);
            }
        }

        @Override
        public void run(Timeout timeout) throws Exception {
            Connection connection = this.connection;

            if (connection == null || !connection.isConnected()) {
                LOG.info("heartbeat timeout times={}, connection disconnected, conn={}", timeoutTimes, connection);
                return;
            }

            if (connection.isReadTimeOut()) {
                if (++timeoutTimes > Config.WebSocketConf.maxTimeoutTimes) {
                    connection.close();
                    // remove redis info
                    User user = dataService.findUser(connection.getId());
                    if (user != null){
                        LOG.info("remove user from redis, user is '{}'", user);
                        dataService.removeUser(user);
                    }

                    LOG.warn("client heartbeat timeout times={}, do close conn={}", timeoutTimes, connection);

                    return;
                } else {
                    LOG.info("client heartbeat timeout times={}, connection={}", timeoutTimes, connection);
                }
            } else {
                timeoutTimes = 0;
            }
            startTimeout();
        }

        @Override
        public void close() {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        }

        @Override
        public Connection get() {
            return connection;
        }
    }

    @FunctionalInterface
    private interface ConnectionHolderFactory {
        ConnectionHolder create(Connection connection);
    }
}
