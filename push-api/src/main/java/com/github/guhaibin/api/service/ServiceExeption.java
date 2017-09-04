package com.github.guhaibin.api.service;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class ServiceExeption extends RuntimeException {
    public ServiceExeption(String message) {
        super(message);
    }

    public ServiceExeption(Throwable cause) {
        super(cause);
    }

    public ServiceExeption(String message, Throwable cause) {
        super(message, cause);
    }
}
