package fr.alexdoru.mwe.http.exceptions;

public class RateLimitException extends ApiException {

    public RateLimitException(String message) {
        super(message);
    }

}
