package com.github.guhaibin.api.service;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public interface Listener {

    void onSuccess(Object ... args);
    void onFailed(Throwable t);
}
