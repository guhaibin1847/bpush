package com.github.guhaibin;

import com.github.guhaibin.api.Config;
import com.github.guhaibin.api.protocol.Command;
import com.github.guhaibin.api.protocol.Flags;
import com.github.guhaibin.api.protocol.Packet;
import com.github.guhaibin.api.spi.common.Json;

import java.util.Properties;
import java.util.function.Consumer;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println(Command.toCMD((byte)6));
    }
}
