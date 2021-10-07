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
    "Wow minute ti chum, t'exagere un peu lo lo.", "T'es tu serieux gros big?", "NICE POWER TRIP BUDDY",
            "Touche moe le mijo", "Va t'coucher ti gars", "SA FAIT DU SENS QU'EST-CE TU DIT", "Julien pacte HAHAHAHA",
    "Yo faut que jte parle live dans un chat prive."};
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
