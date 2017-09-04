package com.github.guhaibin.api.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class BaseService implements Service {

    private AtomicBoolean started = new AtomicBoolean(false);

    protected void startInternal(Listener listener) throws Throwable{
        listener.onSuccess();
    }

    protected void stopInternal(Listener listener){
        listener.onSuccess();
    }

    protected void tryStart(Listener listener, Function func){
        FutureListener fl = wrap(listener);
        if (started.compareAndSet(false, true)) {
            try {
                init();
                func.apply(fl);
                fl.monitor();
            } catch (Throwable t) {
                listener.onFailed(t);
                throw new ServiceExeption(t);
            }
        }else {
            fl.onFailed(new ServiceExeption("service already started"));
        }
    }

    protected void tryStop(Listener listener, Function func){
        FutureListener fl = wrap(listener);
        if (started.compareAndSet(true, false)){
            try {
                func.apply(listener);
                fl.monitor();
            }catch (Throwable e){
                fl.onFailed(e);
                throw new ServiceExeption(e);
            }
        }else {
            fl.onFailed(new ServiceExeption("service already stopped"));
        }
    }

    @Override
    public void start(Listener listener) {
        tryStart(listener, this::startInternal);
    }

    @Override
    public void stop(Listener listener) {
        tryStop(listener, this::stopInternal);
    }

    @Override
    public CompletableFuture<Boolean> start() {
        FutureListener listener = new FutureListener(started);
        start(listener);
        return listener;
    }

    @Override
    public CompletableFuture<Boolean> stop() {
        FutureListener listener = new FutureListener(started);
        stop(listener);
        return listener;
    }

    @Override
    public boolean syncStart() {
        return start().join();
    }

    @Override
    public boolean syncStop() {
        return stop().join();
    }

    @Override
    public void init() {

    }

    @Override
    public boolean isRunning() {
        return started.get();
    }

    private FutureListener wrap(Listener listener){
        if (listener == null){
            return new FutureListener(started);
        }
        if (listener instanceof FutureListener){
            return (FutureListener) listener;
        }
        return new FutureListener(listener, started);
    }

    @FunctionalInterface
    protected interface Function {
        void apply(Listener listener) throws Throwable;
    }
}
