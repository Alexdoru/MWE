package fr.alexdoru.configlib.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Static void methods with no parameters annotated with this will show as a button
 * at the bottom of the category list in the config gui, clicking the button runs the method
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigGuiAction {

    /** The text displayed on the button in the config gui */
    String displayString();

}
