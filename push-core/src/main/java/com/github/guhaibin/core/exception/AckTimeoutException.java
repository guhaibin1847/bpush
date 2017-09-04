package com.github.guhaibin.core.exception;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class AckTimeoutException extends RuntimeException {

    public AckTimeoutException(String message){
        super(message);
    }

    public AckTimeoutException(String message, Throwable t){
        super(message, t);
    }

}
