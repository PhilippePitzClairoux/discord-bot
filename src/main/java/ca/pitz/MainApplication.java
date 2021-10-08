package ca.pitz;

import java.util.List;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.apache.commons.cli.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import javax.security.auth.login.LoginException;

@Component
@SpringBootApplication
public class MainApplication implements CommandLineRunner {

  private final GenericEventListener eventListener;
  private final HelpFormatter helpFormatter = new HelpFormatter();

  @Autowired
  public MainApplication(GenericEventListener eventListener) {
    this.eventListener = eventListener;
  }

  public static void main(String[] args) throws LoginException {
    SpringApplication.run(MainApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    Options options = new Options();
    CommandLineParser parser = new DefaultParser();

    options.addOption("t", "token", true, "Discord token");
    options.addOption("r", "random-events", false, "Enable random events");
    CommandLine cmd = parser.parse(options, args);

    if (!cmd.hasOption("t")) {
      helpFormatter.printHelp("java -jar disc-bot.jar -t xyz", options);
      System.exit(-1);
    }

    JDA jda = JDABuilder
        .createDefault(cmd.getOptionValue("t"), GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
        .addEventListeners(eventListener)
        .build().awaitReady();

    System.out.println(jda.getGuilds().get(1).getMembers());
  }
}
