package fr.alexdoru.mwe.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MultithreadingUtil {

    private static final ExecutorService service = Executors.newFixedThreadPool(10);

    public static <V> Future<V> addTaskToQueue(Callable<V> c) {
        return service.submit(c);
    }

}
