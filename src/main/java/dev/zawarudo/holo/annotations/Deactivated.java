package dev.zawarudo.holo.annotations;

import dev.zawarudo.holo.core.AbstractCommand;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to show that a {@link AbstractCommand} has been deactivated and shouldn't be registered.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Deactivated {
}