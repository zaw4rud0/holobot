package com.xharlock.holo.owner;

import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.core.AbstractCommand;
import com.xharlock.holo.core.CommandCategory;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;

/**
 * Command to set the status of the bot
 */
@Command(name = "status",
		description = "Sets the status of the bot.",
		usage = "[default | listening <message> | watching <message> | playing <message> | competing <message>]",
		ownerOnly = true,
		category = CommandCategory.OWNER)
public class StatusCmd extends AbstractCommand {

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);

		if (args.length == 0) {
			int guilds = e.getJDA().getGuilds().size();
			int users = e.getJDA().getUsers().size();
			e.getJDA().getPresence().setActivity(Activity.listening(users + " users on " + guilds + " servers"));
			return;
		}
		
		if (args[0].equals("default")) {
			e.getJDA().getPresence().setActivity(Activity.watching(getPrefix(e) + "help"));
			return;
		}

		String status = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

		switch (args[0]) {
			case "listening" -> e.getJDA().getPresence().setActivity(Activity.listening(status));
			case "playing" -> e.getJDA().getPresence().setActivity(Activity.playing(status));
			case "watching" -> e.getJDA().getPresence().setActivity(Activity.watching(status));
			case "competing" -> e.getJDA().getPresence().setActivity(Activity.competing(status));
			default -> {}
		}
	}
}