package fr.alexdoru.megawallsenhancementsmod.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Multithreading {

    private static final ExecutorService service = Executors.newFixedThreadPool(10);

    public static void addTaskToQueue(Callable<String> c) {
        service.submit(c);
    }

}
