package com.xharlock.holo.owner;

import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.core.AbstractCommand;
import com.xharlock.holo.core.CommandCategory;
import com.xharlock.holo.core.Bootstrap;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.Instant;
import java.util.Arrays;

@Command(name = "blacklist",
		description = "Blacklists an user from using the bot.",
		usage = "<user id>",
		ownerOnly = true,
		category = CommandCategory.OWNER)
public class BlacklistCmd extends AbstractCommand {

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);
		
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTimestamp(Instant.now());

		// No argument was given
		if (args.length != 1) {
			builder.setTitle("Incorrect Usage");
			builder.setDescription("Please only provide the id of the user you want to blacklist!");
			sendToOwner(e, builder);
			return;
		}
		
		long id;
		
		// Couldn't parse id
		try {
			id = Long.parseLong(args[0].replace("<@!", "").replace(">", ""));
		} catch (NumberFormatException ex) {
			builder.setTitle("Error");
			builder.setDescription("Please provide the id of the user you want to blacklist");
			sendToOwner(e, builder);
			return;
		}
		
		User toBlacklist = e.getJDA().getUserById(id);
		
		// Couldn't find user (probably because bot doesn't share a server with them)
		if (toBlacklist == null) {
			builder.setTitle("Error");
			builder.setDescription("I couldn't find this user");
			sendToOwner(e, builder);
			return;
		}
		
		String reason = "None given";
		
		if (args.length > 1) {
			reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
		}
		
		builder.setTitle("User Blacklisted");
		builder.setDescription("**Name:** " + toBlacklist.getAsMention() + "\n"
						+ "**Tag:** " + toBlacklist.getAsTag() + "\n"
						+ "**Id:** " + toBlacklist.getIdLong() + "\n"
						+ "**Reason:** " + reason);
		
		sendToOwner(e, builder);
		Bootstrap.holo.getPermissionManager().blacklist(toBlacklist);
	}
}