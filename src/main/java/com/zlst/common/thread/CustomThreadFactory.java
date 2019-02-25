package com.zlst.common.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 170137 2019-01-10
 */
public final class CustomThreadFactory implements ThreadFactory {

    private final AtomicInteger index = new AtomicInteger(1);

    private String tName;

    public CustomThreadFactory(String tName) {
        this.tName = tName;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, tName + index.getAndIncrement());
    }
}
