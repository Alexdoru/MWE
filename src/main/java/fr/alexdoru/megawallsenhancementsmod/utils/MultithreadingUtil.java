package fr.alexdoru.megawallsenhancementsmod.utils;

import net.minecraft.util.IChatComponent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MultithreadingUtil {

    private static final ExecutorService service = Executors.newFixedThreadPool(10);

    public static void addTaskToQueue(Callable<String> c) {
        service.submit(c);
    }

    public static Future<IChatComponent> addTaskToQueueAndGetFuture(Callable<IChatComponent> c) {
        return service.submit(c);
    }

}
