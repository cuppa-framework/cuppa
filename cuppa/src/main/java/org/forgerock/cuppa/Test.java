package org.forgerock.cuppa;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifies a class as containing Cuppa tests.
 *
 * <p>Cuppa tests contained with the annotated class will be included during test runs.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Test {
}
