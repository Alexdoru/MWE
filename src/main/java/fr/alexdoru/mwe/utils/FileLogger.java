package fr.alexdoru.mwe.utils;

import net.minecraft.client.Minecraft;

import java.io.*;
import java.text.SimpleDateFormat;

public class FileLogger {

    private final SimpleDateFormat dateFormat;
    private PrintStream printStream = null;

    public FileLogger(File logFolder, String filename) {
        this(logFolder, filename, "HH:mm:ss");
    }

    public FileLogger(String filename, String timeFormat) {
        this(new File(Minecraft.getMinecraft().mcDataDir, "logs"), filename, timeFormat);
    }

    public FileLogger(File logFolder, String filename, String timeFormat) {
        if (timeFormat == null) {
            dateFormat = null;
        } else {
            dateFormat = new SimpleDateFormat(timeFormat);
        }
        if (logFolder == null) {
            throw new IllegalStateException("FileLogger logFolder cannot be null!");
        }
        if (filename == null) {
            throw new IllegalStateException("FileLogger file name cannot be null!");
        }
        //noinspection ResultOfMethodCallIgnored
        logFolder.mkdirs();
        final File logFile = new File(logFolder, filename);
        if (logFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            logFile.delete();
        }
        if (!logFile.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            printStream = new PrintStream(new FileOutputStream(logFile, true));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void log(String message) {
        if (printStream == null) return;
        if (dateFormat == null) {
            printStream.println(message);
            return;
        }
        final String time = dateFormat.format(System.currentTimeMillis());
        printStream.println("[" + time + "] " + message);
    }

}
