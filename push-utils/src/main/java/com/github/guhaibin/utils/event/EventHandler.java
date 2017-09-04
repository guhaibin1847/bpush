package com.github.guhaibin.utils.event;

import com.github.guhaibin.api.event.Event;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public interface EventHandler<T extends Event> {

    void handle(T event);

}
