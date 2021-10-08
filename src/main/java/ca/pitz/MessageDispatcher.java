package ca.pitz;

import ca.pitz.commands.Command;
import ca.pitz.commands.CommandsManager;
import ca.pitz.commands.usable.RandomEvents;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MessageDispatcher {

    private final RandomEvents randomEvents;
    private final List<String> whitelistedChannels = List.of("legends-news", "bot-feature-request", "music");
    private final CommandsManager commands;

    @Autowired
    public MessageDispatcher(RandomEvents randomEvents, CommandsManager commands) {
        this.randomEvents = randomEvents;
        this.commands = commands;
    }



    private String extractCommand(String message) {
        if (message.startsWith("!") && message.contains(" ")) {
            return message.substring(0, message.indexOf(" "));
        }
        return message;
    }

    private List<String> extractArgs(String message) {
        return Arrays.stream(message.split(" (?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)"))
                .filter(s -> !s.startsWith("!"))
                .collect(Collectors.toList());
    }

    void dispatch(MessageReceivedEvent message) throws IOException, InterruptedException, InvocationTargetException, IllegalAccessException {
        String msg = message.getMessage().getContentRaw();
        String channelName = message.getChannel().getName();

        System.out.println("Current channel : " + channelName);
        System.out.println("Current guild : " + message.getGuild().getName());
        if (!whitelistedChannels.contains(channelName)) {
            return;
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
            if (value >= 99) {
                message.getChannel().sendMessage(randomEvents.getRandomMessage(null, null)).queue();
            }
        }
    }
}
