package ca.pitz;

import ca.pitz.commands.Command;
import ca.pitz.commands.CommandHolder;
import ca.pitz.commands.DiscordCommand;
import ca.pitz.commands.usable.RandomEvents;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class MessageDispatcher {

    private final RandomEvents randomEvents;
    private final ApplicationContext context;
    private final Map<String, Command> commands;
    private final List<Object> params = List.of(MessageReceivedEvent.class, List.class);

    @Autowired
    public MessageDispatcher(RandomEvents randomEvents, ApplicationContext context) {
        this.randomEvents = randomEvents;
        this.context = context;
        this.commands = new HashMap<>();

        loadCommands();
    }

    private void loadCommands() {
        Map<String, Object> beans = context.getBeansWithAnnotation(CommandHolder.class);

        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            for (Method method : entry.getValue().getClass().getMethods()) {
                if (method.isAnnotationPresent(DiscordCommand.class)) {
                    if (params.containsAll(Arrays.asList(method.getParameterTypes()))) {
                        DiscordCommand metadata = method.getAnnotation(DiscordCommand.class);
                        commands.put(metadata.name(), Command.builder()
                                .metadata(metadata)
                                .method(method)
                                .instance(entry.getValue())
                                .build());
                    }
                }
            }
        }
    }

    private String extractCommand(String message) {
        if (message.split(" ").length > 1) {
            return message.substring(0, message.indexOf(" "));
        }
        return message;
    }

    private List<String> extractArgs(String message) {
        return Arrays.stream(message.split(" "))
                .filter(s -> !s.startsWith("!"))
                .collect(Collectors.toList());
    }

    void dispatch(MessageReceivedEvent message) throws IOException, InterruptedException, InvocationTargetException, IllegalAccessException {
        String msg = message.getMessage().getContentRaw();
        if (msg.startsWith("!") && !message.getAuthor().isBot()) {
            String commandName = extractCommand(msg);
            List<String> args = extractArgs(msg);

            //find command
            Command command = this.commands.get(commandName);
            //call command
            if (args.size() < Integer.parseInt(command.getMetadata().numberOfArgs())) {
                message.getChannel().sendMessage("This command is invalid boy. Check this out :").queue();
                message.getChannel().sendMessage(command.getMetadata().help()).queue();
                return;
            }

            command.getMethod().invoke(command.getInstance(), message, args);

            //delete the message that sent the command
            message.getChannel().deleteMessageById(message.getMessageId()).queue();
        } else if (!message.getAuthor().isBot()) {
            double value = Math.random() * (100);
            System.out.println("Bot rolled : " + value);
            if (value >= 95) {
                message.getChannel().sendMessage(randomEvents.getRandomMessage(null, null)).queue();
            }
        }
    }
}
