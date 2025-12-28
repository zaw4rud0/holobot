package dev.zawarudo.holo.core;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {

    private final String prefix;
    private final boolean daemon;
    private final AtomicInteger n = new AtomicInteger(1);


    public NamedThreadFactory(String prefix) {
        this(prefix, true);
    }

    public NamedThreadFactory(String prefix, boolean daemon) {
        this.prefix = prefix;
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(@NotNull Runnable r) {
        Thread t = new Thread(r, prefix + "-" + n.getAndIncrement());
        t.setDaemon(daemon);
        return t;
    }
}
