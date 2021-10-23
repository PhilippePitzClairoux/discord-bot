package ca.pitz.utils;

import java.util.List;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.springframework.stereotype.Component;


public class MessageUtils {


  public static void sendMessage(MessageChannel messageChannel, String message) {
    messageChannel.sendMessage(message).queue();
  }

  public static void sendFormattedMessage(MessageChannel messageChannel, List<String> content) {
    String currentRowOutput = "";
    int i = 0;

    do {
      currentRowOutput = currentRowOutput.concat(content.get(i));
      if (i + 1 < content.size()) {
        currentRowOutput = currentRowOutput.concat(" - ");
      }

      i++;
    } while (i < content.size());
    messageChannel.sendMessage(currentRowOutput).queue();
  }

}
