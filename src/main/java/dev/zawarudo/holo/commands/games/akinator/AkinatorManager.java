package dev.zawarudo.holo.commands.games.akinator;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import dev.zawarudo.holo.utils.exceptions.APIException;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages all Akinator instances that are currently running.
 */
public class AkinatorManager {

    private final EventWaiter eventWaiter;
    private final Map<Long, AkinatorInstance> instances;

    public AkinatorManager(EventWaiter eventWaiter) {
        this.eventWaiter = eventWaiter;
        instances = new HashMap<>();
    }

    public AkinatorInstance createInstance(MessageReceivedEvent event) throws APIException {
        AkinatorInstance instance = new AkinatorInstance(event, eventWaiter);
        instances.put(event.getAuthor().getIdLong(), instance);
        return instance;
    }

    /**
     * Checks if a given user already has an Akinator instance.
     *
     * @param userId The id of the user.
     * @return True if the user is already playing Akinator, false otherwise.
     */
    public boolean hasInstance(long userId) {
        return instances.containsKey(userId);
    }

    /**
     * Removes the Akinator instance for a given user.
     *
     * @param userId The id of the user.
     */
    public void removeInstance(long userId) {
        instances.remove(userId);
    }
}