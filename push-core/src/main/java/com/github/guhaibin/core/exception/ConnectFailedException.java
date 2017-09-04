package com.github.guhaibin.core.exception;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class ConnectFailedException extends RuntimeException {

    public ConnectFailedException(String message){
        super(message);
    }

    public ConnectFailedException(String message, Throwable t){
        super(message, t);
    }
}
