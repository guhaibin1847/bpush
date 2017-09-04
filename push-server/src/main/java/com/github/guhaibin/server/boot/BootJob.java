package com.github.guhaibin.server.boot;

import com.github.guhaibin.server.Logs;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public abstract class BootJob {

    private BootJob next;
    private String name;

    public BootJob(String name){
        this.name = name;
    }

    abstract void start();
    abstract void stop();

    public void startNext(){
        if (next != null){
            Logs.BOOTSTRAP_LOG.info("start bootstrap job '{}'", next.name);
            next.start();
        }
    }

    public void stopNext(){
        if (next != null){
            next.stop();
            Logs.BOOTSTRAP_LOG.info("stopped bootstrap job '{}'", next.name);
        }
    }

    public BootJob setNext(BootJob next){
        this.next = next;
        return next;
    }


}
