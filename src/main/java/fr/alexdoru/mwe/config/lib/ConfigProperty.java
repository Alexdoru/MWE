package fr.alexdoru.mwe.config.lib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigProperty {

    /** The name of the category this config property belongs to */
    String category();

    /** The name of the subcategory this config property belongs to, if any */
    String subCategory() default "";

    /** The name of this config property, used as a unique key in forge's property system */
    String name();

    /** A comment with formatting to show in the config menu */
    String comment() default "";

    /** Should this config be hidden from the config gui */
    boolean hidden() default false;

    int sliderMin() default 0;

    int sliderMax() default 0;

    boolean isColor() default false;

}
