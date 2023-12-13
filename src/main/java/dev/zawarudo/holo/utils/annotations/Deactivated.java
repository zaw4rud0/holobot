package dev.zawarudo.holo.utils.annotations;

import dev.zawarudo.holo.commands.AbstractCommand;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to show that an {@link AbstractCommand} has been deactivated
 * and shouldn't be registered on start-up.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Deactivated {
}