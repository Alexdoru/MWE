package fr.alexdoru.mwe.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MultithreadingUtil {

    private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(10, new ThreadFactoryBuilder().setNameFormat("mwe-thread-pool-%d").build());

    public static Future<?> addTaskToQueue(Runnable r) {
        return THREAD_POOL.submit(r);
    }

    public static <V> Future<V> addTaskToQueue(Callable<V> c) {
        return THREAD_POOL.submit(c);
    }

}