package com.github.guhaibin.api.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class FutureListener extends CompletableFuture<Boolean> implements Listener {

    private static final int TIME_OUT_SECS = 10;

    private Listener listener;
    private AtomicBoolean started;

    public FutureListener(AtomicBoolean started) {
        this.started = started;
        this.listener = null;
    }

    public FutureListener(Listener listener, AtomicBoolean started) {
        this.listener = listener;
        this.started = started;
    }

    @Override
    public void onSuccess(Object... args) {
        if (isDone()) {
            return;
        }
        complete(started.get());
        if (listener != null){
            listener.onSuccess(args);
        }
    }

    @Override
    public void onFailed(Throwable t) {
        if (isDone()){
            return;
        }
        completeExceptionally(t);
        if (listener != null){
            listener.onFailed(t);
        }
    }

    public void monitor(){
        if (isDone()){
            return;
        }
        runAsync(() -> {
            try{
                this.get(TIME_OUT_SECS, TimeUnit.SECONDS);
            }catch (Exception e){
                this.onFailed(new ServiceExeption("service do job time out", e));
            }
        });
    }
}
