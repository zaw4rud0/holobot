package dev.zawarudo.holo.commands.owner;

import dev.zawarudo.holo.utils.annotations.CommandInfo;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.core.Bootstrap;
import dev.zawarudo.holo.commands.CommandCategory;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Command to set the status of the bot
 */
@CommandInfo(name = "status",
		description = "Sets the status of the bot.",
		usage = "[default | listening <message> | watching <message> | playing <message> | competing <message>]",
		ownerOnly = true,
		category = CommandCategory.OWNER)
public class StatusCmd extends AbstractCommand {

	@Override
	public void onCommand(@NotNull MessageReceivedEvent e) {
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
			case "listening" -> setActivity(Activity.listening(status));
			case "playing" -> setActivity(Activity.playing(status));
			case "watching" -> setActivity(Activity.watching(status));
			case "competing" -> setActivity(Activity.competing(status));
			default -> {}
		}
	}

	/**
	 * Sets the activity of the bot.
	 */
	private void setActivity(Activity activity) {
		Bootstrap.holo.getJDA().getPresence().setActivity(activity);
	}
}