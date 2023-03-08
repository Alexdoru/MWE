package fr.alexdoru.megawallsenhancementsmod.api.exceptions;

public class RateLimitException extends ApiException {

    public RateLimitException(String message) {
        super(message);
    }

}
