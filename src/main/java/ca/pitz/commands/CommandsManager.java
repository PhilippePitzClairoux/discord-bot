package ca.pitz.commands;

import ca.pitz.commands.usable.HelperCommands;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;

@Slf4j
@Component
public class CommandsManager {

  private final List<Object> params = List.of(MessageReceivedEvent.class, List.class);
  private final ApplicationContext context;
  private HashMap<String, Command> commands;

  @Autowired
  public CommandsManager(ApplicationContext context) {
    this.commands = new HashMap<>();
    this.context = context;
    loadCommands();
  }

  public Command get(String name) {
    return commands.get(name);
  }

  public Set<Map.Entry<String, Command>> getAllArgs() {
    return commands.entrySet();
  }

  private void loadCommands() {
    Map<String, DiscordCommandInterface> beans = context
        .getBeansOfType(DiscordCommandInterface.class);

    for (Map.Entry<String, DiscordCommandInterface> entry : beans.entrySet()) {
      for (Method method : entry.getValue().getClass().getMethods()) {
        if (method.isAnnotationPresent(DiscordCommand.class) && params
            .containsAll(Arrays.asList(method.getParameterTypes())) && entry.getValue()
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

    //static import of help command
    Method method = Arrays.stream(HelperCommands.class.getMethods()).sequential()
        .filter(methodIt -> methodIt.isAnnotationPresent(DiscordCommand.class))
        .findFirst().orElseGet(null);

    DiscordCommand metadata = method.getAnnotation(DiscordCommand.class);

    commands.put("!help", Command.builder()
        .metadata(metadata)
        .method(method)
        .instance(new HelperCommands(this))
        .build());

  }

}
