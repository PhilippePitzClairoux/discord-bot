package ca.pitz.commands;

import lombok.Builder;
import lombok.Getter;

import java.lang.reflect.Method;

@Getter
@Builder
public class Command {
    private Method method;
    private DiscordCommand metadata;
    private Object instance;
}
