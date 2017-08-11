package org.team4u.diff.definiton;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Jay Wu
 */
@Target({FIELD, TYPE})
@Retention(RUNTIME)
@Inherited
public @interface Definition {

    String value();

    boolean id() default false;

    Class<?>[] refer() default {};

    String formatter() default "";
}