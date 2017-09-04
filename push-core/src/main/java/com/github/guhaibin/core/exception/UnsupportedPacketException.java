package com.github.guhaibin.core.exception;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class UnsupportedPacketException extends RuntimeException {
    public UnsupportedPacketException(String message) {
        super(message);
    }

    public UnsupportedPacketException(String message, Throwable cause) {
        super(message, cause);
    }
}
