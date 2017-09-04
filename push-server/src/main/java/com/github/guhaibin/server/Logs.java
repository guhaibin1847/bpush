package com.github.guhaibin.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public interface Logs {
    Logger HEART_BEAT = LoggerFactory.getLogger("HeartBeat");
    Logger BOOTSTRAP_LOG = LoggerFactory.getLogger("BootStrap");
}
