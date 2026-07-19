package fr.alexdoru.mwe.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class MultithreadingUtil {

    private MultithreadingUtil() {}

    private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(
            10,
            new ThreadFactoryBuilder()
                    .setNameFormat("mwe-thread-pool-%d")
                    .setDaemon(true)
                    .build()
    );

    private static final ExecutorService IO_THREAD = Executors.newSingleThreadExecutor(r -> {
        final Thread thread = new Thread(r, "mwe-io-thread");
        thread.setDaemon(true);
        return thread;
    });

    public static Future<?> addTaskToQueue(Runnable r) {
        return THREAD_POOL.submit(r);
    }

    public static <V> Future<V> addTaskToQueue(Callable<V> c) {
        return THREAD_POOL.submit(c);
    }

    public static void queueIOTask(Runnable r) {
        IO_THREAD.submit(r);
    }

}