package com.github.guhaibin.api.push;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class PushResult {
    public static final int SUCCESS = 1;
    public static final int FAILELD = 2;
    public static final int TIME_OUT = 3;
    public static final int OFF_LINE = 4;

    private int result;
    private String toId;

    public PushResult(int result, String toId) {
        this.result = result;
        this.toId = toId;
    }

    public int getResult() {
        return result;
    }

    public String getToId() {
        return toId;
    }

    @Override
    public String toString() {
        return "PushResult{" +
                "result=" + result +
                ", toId='" + toId + '\'' +
                '}';
    }
}
