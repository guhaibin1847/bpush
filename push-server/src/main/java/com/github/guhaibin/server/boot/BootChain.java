package com.github.guhaibin.server.boot;

import com.github.guhaibin.server.Logs;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class BootChain {

    private final BootJob first = new BootJob("firstJob") {
        @Override
        void start() {
            Logs.BOOTSTRAP_LOG.info("bootstrap starting...");
            startNext();
        }

        @Override
        void stop() {
            stopNext();
            Logs.BOOTSTRAP_LOG.info("bootstrap stopped.");
            Logs.BOOTSTRAP_LOG.info("===================================================================");
            Logs.BOOTSTRAP_LOG.info("====================JYPUSH SERVER STOPPED SUCCESS==================");
            Logs.BOOTSTRAP_LOG.info("===================================================================");
        }
    };

    private BootJob last = first;

    public void start(){
        first.start();
    }

    public void stop(){
        first.stop();
    }

    public BootChain end(){
        setNext(new BootJob("lastJob") {
            @Override
            void start() {
                Logs.BOOTSTRAP_LOG.info("bootstrap started.");
                Logs.BOOTSTRAP_LOG.info("===================================================================");
                Logs.BOOTSTRAP_LOG.info("====================JYPUSH SERVER START SUCCESS====================");
                Logs.BOOTSTRAP_LOG.info("===================================================================");
            }

            @Override
            void stop() {
                Logs.BOOTSTRAP_LOG.info("bootstrap stopping...");
            }
        });
        return this;
    }

    public static BootChain chain(){
        return new BootChain();
    }

    public BootChain setNext(BootJob nextJob){
        this.last = last.setNext(nextJob);
        return this;
    }
}
