package fr.alexdoru.configlib.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods annotated with this will be executed when the config version changes.
 * The signature of the method must be two strings, the handler method will receive
 * the version the config was saved with and the current version.
 * <p>
 * <pre>
 * {@code
 *     @ConfigUpdate
 *     private static void onModUpdate(String savedVersion, String version) {
 *         // code that runs on mod version update
 *     }
 * }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigUpdatedEvent {}
