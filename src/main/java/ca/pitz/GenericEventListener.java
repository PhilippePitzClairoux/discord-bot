package ca.pitz;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@Slf4j
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
            log.error("Exception occured during dispatch...");
            log.error("Exception details : ", e);
        }
    }
}
