package com.github.guhaibin.core.exception;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class UnsupportUserTypeException extends RuntimeException {
    public UnsupportUserTypeException(String message) {
        super(message);
    }

    public UnsupportUserTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
