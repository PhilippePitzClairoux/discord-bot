package ca.pitz.commands.usable;


import ca.pitz.commands.Command;
import ca.pitz.commands.CommandsManager;
import ca.pitz.commands.DiscordCommand;
import ca.pitz.commands.DiscordCommandInterface;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.Map;

@Slf4j
public class HelperCommands implements DiscordCommandInterface {

    private final CommandsManager commandsManager;

    public HelperCommands(CommandsManager commandsManager) {
        this.commandsManager = commandsManager;
    }

    @Override
    public boolean commandIsAvailable() {
        return true;
    }

    @DiscordCommand(name = "!help", numberOfArgs = "0", help = "!help (lists all commands available to you)")
    public void help(MessageReceivedEvent message, List<String> args)  {
        String buffer = "";
        for (Map.Entry<String, Command> entry : commandsManager.getAllArgs()) {
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
}
