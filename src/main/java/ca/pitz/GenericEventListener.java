package ca.pitz;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@Component
public class GenericEventListener extends ListenerAdapter {

    private final MessageDispatcher messageDispatcher;

    @Autowired
    public GenericEventListener(MessageDispatcher messageDispatcher) {
        this.messageDispatcher = messageDispatcher;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        try {
            messageDispatcher.dispatch(event);
        } catch (IOException | InterruptedException | InvocationTargetException | IllegalAccessException e) {
            System.out.println("Exception occured during dispatch...");
            System.out.println(e);
        }
    }
}
