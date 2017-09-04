package com.github.guhaibin;

import com.github.guhaibin.api.push.*;
import com.github.guhaibin.client.WebSocketClient;
import com.github.guhaibin.client.WebSocketPusher;
import com.github.guhaibin.utils.Jsons;

import java.util.concurrent.CountDownLatch;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws InterruptedException {
        Pusher pusher = new WebSocketPusher(new MessageReceiver() {
            @Override
            public void onNew() {
                System.out.println("new");
            }

            @Override
            public void onConnected() {
                System.out.println("connected");
            }

            @Override
            public int onReceive(SendMessage message) {
                System.out.println("receive message, " + message);
                System.out.println(System.currentTimeMillis());
                return 1;
            }

            @Override
            public void onInactive() {
                System.out.println("inactive");
            }
        }, UserType.CLIENT_USER, "Bean");
        pusher.start();

        CountDownLatch latch = new CountDownLatch(1);
        System.out.println(System.currentTimeMillis());
        pusher.syncPush(new SendMessage("s", "xx"), UserType.CLIENT_USER, "store_111", new PushCallback() {
            @Override
            public void onResult(PushResult result) {
                System.out.println(result);
                System.out.println("xx");
                latch.countDown();
            }
        });
        System.out.println(System.currentTimeMillis());
        latch.await();
        pusher.close();
    }
}
