package ca.pitz.commands.usable;

import ca.pitz.commands.CommandHolder;
import ca.pitz.commands.DiscordCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

@CommandHolder
public class RandomEvents {

    private final String[] events = {"QUI PARLE?", "Yeaaaaah Yeeeaaaaaah Yeah.", "Tu cherche le beef mec?",
            "BRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR", "Aye big t'es tu pacte?",
    "Wow minute ti chum, t'exagere un peu lo lo."};

    @DiscordCommand(numberOfArgs = "0", help = "NOPE!")
    public String getRandomMessage(MessageReceivedEvent message, List<String> args) {
        int index = (int) (Math.random() * (events.length));
        System.out.println("Index : " + index);
        return events[index];
    }

}
