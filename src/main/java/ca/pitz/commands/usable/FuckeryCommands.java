package ca.pitz.commands.usable;

import ca.pitz.commands.CommandHolder;
import ca.pitz.commands.DiscordCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

@CommandHolder
public class FuckeryCommands {

    @DiscordCommand(name = "!tyl", help = "!tyl [name]")
    public void tylCommand(MessageReceivedEvent message, List<String> args) {
        message.getChannel().sendMessage(args.get(0)
                .concat(", ta yeule. Avec beaucoup d'amour <3")).queue();
    }

    @DiscordCommand(name = "!stfu", numberOfArgs = "2", help = "!stfu [name] [true|false] (true = sign your message)")
    public void stfuCommand(MessageReceivedEvent message, List<String> args) {
        String m = "%s farme ta yeule tata";
        if (args.get(1).equals("true")) {
            m = message.getAuthor().getAsTag().concat(" fait dire : ").concat(m);
        }
        message.getChannel().sendMessage(String.format(m, args.get(0))).queue();
    }

    @DiscordCommand(name = "!grats", help = "!grats [name]")
    public void gratsCommand(MessageReceivedEvent message, List<String> args) {
        message.getChannel()
                .sendMessage("Grats ".concat(args.get(0)).concat("!!!!!"))
                .queue();
        message.getChannel()
                .sendMessage("https://media.tenor.com/images/a1a9560e87fca898eac66f41c9551f95/tenor.gif")
                .queue();
    }
}
