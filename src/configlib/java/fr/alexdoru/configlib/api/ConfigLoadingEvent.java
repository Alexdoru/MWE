package fr.alexdoru.configlib.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods annotated with this will be called when the config class
 * is being loaded, it fires before any field are read/assigned.
 * The handler method will receive a reference to the configuration file.
 * <p>
 * <pre>
 * {@code
 *     @ConfigLoadingEvent
 *     private static void onConfigLoading(Configuration config) {
 *         // code that runs when the config is loading
 *     }
 * }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigLoadingEvent {}
