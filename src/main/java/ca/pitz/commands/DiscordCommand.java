package ca.pitz.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DiscordCommand {
    String name() default "";
    String numberOfArgs() default "1";
    String help() default "!example [arg1] [arg2]";
}
