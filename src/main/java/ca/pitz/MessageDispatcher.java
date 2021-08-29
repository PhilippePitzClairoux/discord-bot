package ca.pitz;

import ca.pitz.commands.Command;
import ca.pitz.commands.DiscordCommand;
import ca.pitz.commands.DiscordCommandInterface;
import ca.pitz.commands.usable.RandomEvents;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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
        Map<String, DiscordCommandInterface> beans = context.getBeansOfType(DiscordCommandInterface.class);

        for (Map.Entry<String, DiscordCommandInterface> entry : beans.entrySet()) {
            for (Method method : entry.getValue().getClass().getMethods()) {
                if (method.isAnnotationPresent(DiscordCommand.class)) {
                    if (params.containsAll(Arrays.asList(method.getParameterTypes())) && entry.getValue()
                        .commandIsAvailable()) {
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

        if (msg.contains("!help") && !message.getAuthor().isBot()) {
            String buffer = "";
            for (Map.Entry<String, Command> entry : commands.entrySet()) {
                Command command = entry.getValue();
                if (!command.getMetadata().name().isEmpty()) {
                    buffer = buffer.concat(entry.getValue().getMetadata().help())
                            .concat("\n");
                }
            }

            final String helpMessage = buffer;
            message.getAuthor()
                    .openPrivateChannel()
                    .flatMap(privateChannel -> privateChannel.sendMessage(helpMessage))
                    .queue();
        }

        if (msg.startsWith("!") && !message.getAuthor().isBot() && !message.getChannelType().equals(ChannelType.PRIVATE)) {
            String commandName = extractCommand(msg);
            List<String> args = extractArgs(msg);

            //find command
            Command command = this.commands.get(commandName);

            //if command dosent existe, go next
            if (Objects.isNull(command)) {
                message.getChannel().sendMessage("Invalid command. Use `!help` for more information").queue();
                return;
            }

            //call command
            if (args.size() < Integer.parseInt(command.getMetadata().numberOfArgs())) {
                message.getChannel().sendMessage("This command is invalid boy. Check this out :").queue();
                message.getChannel().sendMessage(command.getMetadata().help()).queue();
                return;
            }

            command.getMethod().invoke(command.getInstance(), message, args);

        } else if (!message.getAuthor().isBot()) {
            double value = Math.random() * (100);
            log.debug("Bot rolled : " + value);
            if (value >= 95) {
                message.getChannel().sendMessage(randomEvents.getRandomMessage(null, null)).queue();
            }
        }
    }
}
