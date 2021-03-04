package ca.pitz.commands.usable;

import ca.pitz.commands.CommandHolder;
import ca.pitz.commands.DiscordCommand;
import ca.pitz.utils.ServerUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;


@CommandHolder
public class ValheimServerCommands {

    private final ServerUtils serverUtils;

    @Autowired
    public ValheimServerCommands(ServerUtils serverUtils) {
        this.serverUtils = serverUtils;
    }

    @DiscordCommand(name = "!server_status", numberOfArgs = "0", help = "!server_status (returns server status)")
    public void testCommand(MessageReceivedEvent message, List<String> args) throws IOException {
        message.getChannel().sendMessage(serverUtils.getServerStatus()).queue();
    }

    @DiscordCommand(name = "!server_restart", numberOfArgs = "0", help = "!server_restart (restarts server)")
    public void serverRestart(MessageReceivedEvent message, List<String> args) throws IOException, InterruptedException {
        message.getChannel().sendMessage(message.getAuthor().getAsTag()
                .concat(" is requesting to close server..."))
                .queue();
        serverUtils.restartServer();
        message.getChannel().sendMessage("Restarting server...Please wait a few seconds!").queue();
    }

    @DiscordCommand(name = "!server_update", numberOfArgs = "0", help = "!server_update (update server)")
    public void updateServer(MessageReceivedEvent message, List<String> args) throws IOException, InterruptedException {
        message.getChannel().sendMessage(message.getAuthor().getAsTag()
                .concat(" is requesting a server update..."))
                .queue();
        serverUtils.updateServerAndRestart();
        message.getChannel().sendMessage("The server has been updated and was restarted. Enjoy!").queue();
    }

}
