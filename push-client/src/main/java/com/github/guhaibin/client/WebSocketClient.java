package com.github.guhaibin.client;

import com.github.guhaibin.api.Config;
import com.github.guhaibin.api.MessageDispatcher;
import com.github.guhaibin.api.MessageHandler;
import com.github.guhaibin.api.connection.Connection;
import com.github.guhaibin.api.protocol.Command;
import com.github.guhaibin.api.protocol.Packet;
import com.github.guhaibin.api.push.MessageReceiver;
import com.github.guhaibin.api.push.User;
import com.github.guhaibin.api.service.Client;
import com.github.guhaibin.client.handler.AckMessageHandler;
import com.github.guhaibin.client.handler.LoginResponseHandler;
import com.github.guhaibin.client.handler.MessageReceiveHandler;
import com.github.guhaibin.core.exception.AuthenticationException;
import com.github.guhaibin.core.exception.ConnectFailedException;
import com.github.guhaibin.core.message.LoginMessage;
import com.github.guhaibin.netty.connection.NettyConnection;
import com.github.guhaibin.netty.handler.ClientReconnectHandler;
import com.github.guhaibin.netty.handler.ClientWebSocketHandler;
import com.github.guhaibin.utils.NetWork;
import com.github.guhaibin.utils.common.IdGen;
import com.github.guhaibin.utils.concurrency.StatusCountDownLatch;
import com.github.guhaibin.utils.thread.pool.ThreadPoolManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class WebSocketClient implements Client {

    private static final Logger LOG = LoggerFactory.getLogger(WebSocketClient.class);

    private static final String HOST = Config.WebSocketConf.host;
    private static final int PORT = Config.WebSocketConf.port;
    private static final String PATH = Config.WebSocketConf.wsPath;
    private static final String URL = "ws://" + HOST + ":" + PORT + PATH;
    private static final URI uri = URI.create(URL);

    private Executor executor = ThreadPoolManager.I.getClientExecutor();
    private MessageDispatcher dispatcher = new MessageDispatcher(executor);
    private volatile ClientWebSocketHandler handler;
    private final Connection connection = new NettyConnection();
    private Bootstrap bootstrap;
    private EventLoopGroup eventLoopGroup;
    private ScheduledFuture heartBeatFuture;
    private final MessageReceiver receiver;
    private volatile boolean closed = false;
    private User user;

    public WebSocketClient(MessageReceiver receiver,
                           User user){
        this.receiver = receiver;
        this.user = user;
        init();
    }


    private void createClient(){
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap.group(eventLoopGroup)
                 .channel(NioSocketChannel.class)
                 .handler(new ChannelInitializer<Channel>() {
                     @Override
                     protected void initChannel(Channel ch) throws Exception {
                         initPipeline(ch.pipeline());
                     }
                 });
    }

    private void init(){
        /************************* init message handler ***********************/
        registerHandler(Command.LOGIN, new LoginResponseHandler());
        registerHandler(Command.MESSAGE, new MessageReceiveHandler(receiver, executor));
        registerHandler(Command.ACK, new AckMessageHandler());
        /************************* init channel handler ***********************/
        initChannelHandler();
        createClient();
        receiver.onNew();
    }

    private void initChannelHandler(){
        WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(
                uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()
        );
        handler = new ClientWebSocketHandler(handshaker, dispatcher, connection);
    }

    private void initOptions(){

    }

    private void initPipeline(ChannelPipeline pipeline){
        pipeline.addFirst("reconnect", new ClientReconnectHandler(this));
        pipeline.addLast(new HttpClientCodec());
        pipeline.addLast(new HttpObjectAggregator(65535));
        pipeline.addLast(WebSocketClientCompressionHandler.INSTANCE);
        pipeline.addLast("client-handler", handler);
    }

    public void connect(){
        try {
            Channel channel = bootstrap.connect(new InetSocketAddress(HOST, PORT)).sync().channel();
            connection.init(channel);
            handler.handshakeFuture().sync();
            LOG.info("hand shaker success");
        }catch (Exception e){
            LOG.error("client connect error", e);
            throw new ConnectFailedException("connect error", e);

        }
    }

    private void authenticate(){
        LOG.info("begin authenticate");
        //String username = Config.ClientConf.username;
        String password = IdGen.genPwd(user);
        LoginMessage msg = new LoginMessage(user.getUserType(), user.getUsername(), password);
        connection.send(msg.to());
        StatusCountDownLatch latch = new StatusCountDownLatch(msg.getSessionId(), 1);
        latch.await();
        if(!latch.isSuccess()){
            throw new AuthenticationException("认证失败, 用户：" + user + ", 密码：" + password);
        }
    }

    private void heartBeat(){
        LOG.info("begin heart beat");
        heartBeatFuture = connection.getChannel().eventLoop().scheduleWithFixedDelay(() -> {

                                LOG.trace("heart beat. Time is '{}'", System.currentTimeMillis());

                                connection.send(Packet.HEART_BEAT_PACKET);
                           },
                            0, Config.WebSocketConf.heartBeatInterval, TimeUnit.SECONDS);
    }


    public void start(){
        boolean connect = false;
        try {
            connect();
            connect = true;
        }catch (Exception e){
            LOG.info("connect failed in start, try to reconnect");
            eventLoopGroup.schedule(
                    this::start,
                    Config.WebSocketConf.clientReconnectInterval,
                    TimeUnit.SECONDS
            );
        }
        if (connect) {
            authenticate();
            LOG.info("client start up successfully!");
            // heart beat
            heartBeat();

            receiver.onConnected();
        }
    }


    public void registerHandler(Command command, MessageHandler handler){
        this.dispatcher.register(command, handler);
    }

    public void beforeReconnect(){
        LOG.info("client reconnect");
        LOG.info("1. stop heat beat");
        if (heartBeatFuture != null && !heartBeatFuture.isDone()) {
            heartBeatFuture.cancel(true);
        }
        LOG.info("2. renew channel handler");
        initChannelHandler();
    }

    public void reconnect(){

        LOG.info("3. reconnect to server");
        connect();

        LOG.info("4. re-authenticate");
        authenticate();
        LOG.info("client reconnect successfully!");
        // heart beat
        heartBeat();

        receiver.onConnected();
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public void close() {
        LOG.info("close the client '{}'", getInfo());
        closed = true;
        connection.close();
        eventLoopGroup.shutdownGracefully();
    }


    @Override
    public String getInfo() {
        return "IP is " + NetWork.getLocalIp();
    }

    @Override
    public byte getStatus() {
        return this.connection.getStatus();
    }

    public MessageReceiver getReceiver(){
        return this.receiver;
    }

}
