package dev.zawarudo.holo.games.akinator;

import com.github.markozajc.akiwrapper.Akiwrapper;
import com.github.markozajc.akiwrapper.AkiwrapperBuilder;
import com.github.markozajc.akiwrapper.core.exceptions.ServerNotFoundException;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import dev.zawarudo.holo.exceptions.APIException;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class AkinatorInstance {

    private final Akiwrapper instance;
    private final MessageReceivedEvent event;
    private final EventWaiter waiter;

    public AkinatorInstance(MessageReceivedEvent event, EventWaiter waiter) throws APIException {
        this.event = event;
        this.waiter = waiter;

        try {
            instance = new AkiwrapperBuilder().build();
        } catch (ServerNotFoundException e) {
            throw new APIException(e.getMessage(), e);
        }
    }

    public void start() {
        event.getChannel().sendMessage("New Akinator game").queue();
    }
}