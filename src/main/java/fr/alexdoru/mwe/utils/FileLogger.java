package fr.alexdoru.mwe.utils;

import net.minecraft.launchwrapper.Launch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class FileLogger {

    private static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
    private final DateTimeFormatter timeFormatter;
    private final BufferedWriter writer;

    public FileLogger(String filename) {
        this(filename, DEFAULT_TIME_FORMAT);
    }

    public FileLogger(String filename, String timeFormat) {
        this(new File(Launch.minecraftHome, "logs"), filename, timeFormat);
    }

    public FileLogger(File logFolder, String filename) {
        this(logFolder, filename, DEFAULT_TIME_FORMAT);
    }

    public FileLogger(File logFolder, String filename, String timeFormat) {
        Objects.requireNonNull(logFolder, "logFolder cannot be null");
        Objects.requireNonNull(filename, "filename cannot be null");
        this.timeFormatter = timeFormat == null ? null : DateTimeFormatter.ofPattern(timeFormat);
        BufferedWriter w = null;
        try {
            final Path folder = logFolder.toPath();
            Files.createDirectories(folder);
            w = Files.newBufferedWriter(folder.resolve(filename), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.writer = w;
    }

    public synchronized void log(String message) {
        if (writer == null) return;
        try {
            if (timeFormatter != null) {
                writer.write('[');
                writer.write(timeFormatter.format(LocalTime.now()));
                writer.write("] ");
            }
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}