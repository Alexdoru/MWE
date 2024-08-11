package fr.alexdoru.mwe.config.lib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigCategory {

    /** The name of this category to display in the config menu */
    String displayname();

    /** A comment that will show in the config gui */
    String comment() default "";

}
