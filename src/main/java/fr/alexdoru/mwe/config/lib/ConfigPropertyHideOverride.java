package fr.alexdoru.mwe.config.lib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigPropertyHideOverride {

    /** The names of the config settings that use this condition to hide from the config gui screen */
    String[] name();

}
