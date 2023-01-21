package fr.alexdoru.megawallsenhancementsmod.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigProperty {

    /** The name of the category this config property belongs to */
    String category();

    /** The name of this config property, used as a unique key in forge's property system */
    String name();

    /** A description of what this config property does */
    String comment();

}
