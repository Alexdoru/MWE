package fr.alexdoru.mwe.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import fr.alexdoru.mwe.MWE;

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
        return THREAD_POOL.submit(logExceptions(r));
    }

    public static <V> Future<V> addTaskToQueue(Callable<V> c) {
        return THREAD_POOL.submit(logExceptions(c));
    }

    public static void queueIOTask(Runnable r) {
        IO_THREAD.submit(logExceptions(r));
    }

    // submit() swallows the exceptions and doesn't show
    // in the log so we manually log it
    private static Runnable logExceptions(Runnable r) {
        return () -> {
            try {
                r.run();
            } catch (Throwable t) {
                MWE.logger.error("Uncaught exception in async task", t);
                throw t;
            }
        };
    }

    private static <V> Callable<V> logExceptions(Callable<V> c) {
        return () -> {
            try {
                return c.call();
            } catch (Throwable t) {
                MWE.logger.error("Uncaught exception in async task", t);
                throw t;
            }
        };
    }

}