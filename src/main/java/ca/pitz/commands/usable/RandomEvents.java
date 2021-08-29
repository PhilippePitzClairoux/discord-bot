package ca.pitz.commands.usable;

import ca.pitz.commands.DiscordCommand;
import ca.pitz.commands.DiscordCommandInterface;
import java.util.Random;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RandomEvents implements DiscordCommandInterface {

    private final String[] events = {"QUI PARLE?", "Yeaaaaah Yeeeaaaaaah Yeah.", "Tu cherche le beef mec?",
            "BRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR", "Aye big t'es tu pacte?",
    "Wow minute ti chum, t'exagere un peu lo lo."};
    private final java.util.Random random = new Random();


    @DiscordCommand(numberOfArgs = "0", help = "NOPE!")
    public String getRandomMessage(MessageReceivedEvent message, List<String> args) {
        int index = random.nextInt(events.length);
        return events[index];
    }

    @Override
    public boolean commandIsAvailable() {
        return true;
    }
}
