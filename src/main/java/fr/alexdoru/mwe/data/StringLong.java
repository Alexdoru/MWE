package fr.alexdoru.mwe.data;

/**
 * A simple class to encapsulate a String and a timestamp in the same object
 */
public class StringLong {

    public final long timestamp;
    public final String message;

    public StringLong(long timestamp, String message) {
        this.timestamp = timestamp;
        this.message = message;
    }

}
