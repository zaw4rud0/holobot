package com.xharlock.holo.commands.owner;

import java.util.List;
import java.util.stream.Collectors;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;
import com.xharlock.holo.core.Bootstrap;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PurgeCmd extends Command {

	public PurgeCmd(String name) {
		super(name);
		setDescription("(Owner-only) Use this command to purge a given amount of your messages");
		setUsage(name + " <amount>");
		setIsOwnerCommand(true);
		setCommandCategory(CommandCategory.OWNER);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {

		// TODO Select messages of author
		
		int limit = 100;
		long id = Bootstrap.holo.getConfig().getOwnerId();
		int n = 10;
		
		TextChannel c = (TextChannel) e.getChannel();
		
		c.getIterableHistory().takeAsync(limit).thenApply(list -> list.stream().filter(m -> m.getAuthor().getIdLong() == id).limit(n).collect(Collectors.toList())).thenAccept(msgs -> c.deleteMessages(msgs).queue());
		
		
		int amount = 0;
		
		try { amount = Integer.parseInt(args[0]); } catch (NumberFormatException ex) {}
		
		if (amount > 100) {
			e.getAuthor().openPrivateChannel().complete().sendMessage("You can't purge more than 100 messages at once!").queue();
			return;
		}
		
		List<Message> messagesToPurge = e.getChannel().getHistory().retrievePast(amount).complete();		
		e.getChannel().purgeMessages(messagesToPurge);
	}

}
