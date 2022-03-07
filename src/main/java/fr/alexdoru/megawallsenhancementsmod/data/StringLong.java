package fr.alexdoru.megawallsenhancementsmod.data;

/**
 * A simple class to encapsulate a String and a timestamp in the same object
 */
public class StringLong {

    public long timestamp;
    public String message;

    public StringLong(long timestamp, String message) {
        this.timestamp = timestamp;
        this.message = message;
    }

}
