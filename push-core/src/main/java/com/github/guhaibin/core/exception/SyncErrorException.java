package com.github.guhaibin.core.exception;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class SyncErrorException extends RuntimeException {
    public SyncErrorException(String message) {
        super(message);
    }

    public SyncErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
