package com.github.guhaibin.core.exception;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message){
        super(message);
    }

    public AuthenticationException(String message, Throwable t){
        super(message, t);
    }

}
