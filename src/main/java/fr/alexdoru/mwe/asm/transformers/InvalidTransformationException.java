package fr.alexdoru.mwe.asm.transformers;

public final class InvalidTransformationException extends RuntimeException {

    public InvalidTransformationException() {}

    public InvalidTransformationException(String message) {
        super(message);
    }

}
