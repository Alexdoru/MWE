package fr.alexdoru.configlib.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods annotated with this will be called when the associated config is switched
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigPropertyEvent {

    /** The names of the config settings that when switched will trigger this method event */
    String[] name();

}
