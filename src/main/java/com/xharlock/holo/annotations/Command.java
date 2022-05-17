package com.xharlock.holo.annotations;

import com.xharlock.holo.core.CommandCategory;
import com.xharlock.holo.misc.EmbedColor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to pass information about a command
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    /**
     * The name of the command.
     */
    String name();

    /**
     * The description of the command.
     */
    String description();

    /**
     * The usage of the command.
     */
    String usage() default "";

    /**
     * An example of how to use the command.
     */
    String example() default "";

    /**
     * The aliases of the command.
     */
    String[] alias() default {};

    /**
     * The thumbnail of the command.
     */
    String thumbnail() default "";

    /**
     * The color the embeds should have.
     */
    EmbedColor embedColor() default EmbedColor.DEFAULT;

    /**
     * The category of the command.
     */
    CommandCategory category() default CommandCategory.BLANK;

    /**
     * Whether this command can only be used in a guild. By default, this is true.
     */
    boolean guildOnly() default true;

    /**
     * Whether this command can only be used by the guild administrator. By default, this is false.
     */
    boolean adminOnly() default false;

    /**
     * Whether this command can only be used by the bot owner. By default, this is false.
     */
    boolean ownerOnly() default false;

    /**
     * Whether this command is NSFW (not safe for work). By default, this is false.
     */
    boolean isNSFW() default false;
}