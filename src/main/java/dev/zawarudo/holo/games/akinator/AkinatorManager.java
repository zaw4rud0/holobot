package dev.zawarudo.holo.games.akinator;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.markozajc.akiwrapper.core.entities.Server.GuessType;
import dev.zawarudo.holo.exceptions.APIException;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * A manager that keeps track of all the Akinator games that are currently running.
 */
public class AkinatorManager {

	private final EventWaiter waiter;
	private final Map<Long, Akinator> instances;

	public AkinatorManager(EventWaiter waiter) {
		this.waiter = waiter;
		instances = new HashMap<>();
	}
	
	public Map<Long, Akinator> getInstances() {
		return instances;
	}
	
	public Akinator createInstance(MessageReceivedEvent ev) throws APIException {
		Akinator akinator = new Akinator(ev, this, waiter);
		instances.put(ev.getAuthor().getIdLong(), akinator);
		return akinator;
	}
	
	public Akinator createInstance(MessageReceivedEvent ev, GuessType type) throws APIException {
		Akinator akinator = new Akinator(ev, type, this, waiter);
		instances.put(ev.getAuthor().getIdLong(), akinator);
		return akinator;
	}
	
	public boolean hasInstance(long userId) {
		return instances.containsKey(userId);
	}
	
	public void removeInstance(long userId) {
		instances.remove(userId);
	}
}