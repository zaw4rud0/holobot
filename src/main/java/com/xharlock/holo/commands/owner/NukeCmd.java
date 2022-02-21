package com.xharlock.holo.commands.owner;

import java.util.ArrayList;
import java.util.List;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class NukeCmd extends Command {

	public NukeCmd(String name) {
		super(name);
		setDescription("(Owner-only) Use this command to indiscriminately delete a given amount of messages.");
		setUsage(name + " <amount>");
		setIsGuildOnlyCommand(true);
		setIsOwnerCommand(true);
		setCommandCategory(CommandCategory.OWNER);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {		
		int amount = 0;

		try { amount = Integer.parseInt(args[0]);
		} catch (NumberFormatException ex) {return;}
		
		if (amount < 2) {
			return;
		}		

		int remaining = amount;
		List<Message> messagesToNuke = new ArrayList<>();
		
		while (remaining > 0) {
			if (remaining > 100) {
				messagesToNuke.addAll(e.getTextChannel().getHistory().retrievePast(100).complete());
				e.getTextChannel().deleteMessages(messagesToNuke).queue();
				messagesToNuke.clear();
				remaining -= 100;
			} else {
				messagesToNuke.addAll(e.getTextChannel().getHistory().retrievePast(remaining).complete());
				e.getTextChannel().deleteMessages(messagesToNuke).queue();
				messagesToNuke.clear();
				remaining = 0;
			}
		}
	}
}