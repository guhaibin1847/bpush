package com.github.guhaibin.core.exception;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class SyncTimeoutException extends RuntimeException {

    public SyncTimeoutException(String message) {
        super(message);
    }

    public SyncTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
