package com.github.guhaibin.core.exception;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class ErrorPacketException extends RuntimeException {
    public ErrorPacketException(String message) {
        super(message);
    }

    public ErrorPacketException(String message, Throwable cause) {
        super(message, cause);
    }
}
