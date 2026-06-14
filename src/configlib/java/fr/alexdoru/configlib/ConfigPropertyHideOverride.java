package fr.alexdoru.configlib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods annotated with this must return a boolean, if this method
 * returns true, the associated config fields will be hidden from the config gui
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigPropertyHideOverride {

    /** The names of the config settings that use this condition to hide from the config gui screen */
    String[] name();

}
