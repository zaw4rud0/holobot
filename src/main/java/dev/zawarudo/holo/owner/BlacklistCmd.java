package dev.zawarudo.holo.owner;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.Bootstrap;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.core.PermissionManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Arrays;

/**
 * Command to blacklist a user from using the bot.
 */
@Command(name = "blacklist",
		description = "Blacklists an user from using the bot.",
		usage = "<user id>",
		ownerOnly = true,
		category = CommandCategory.OWNER)
public class BlacklistCmd extends AbstractCommand {

	private final PermissionManager permissionManager;

	public BlacklistCmd() {
		permissionManager = Bootstrap.holo.getPermissionManager();
	}

	@Override
	public void onCommand(@NotNull MessageReceivedEvent event) {
		deleteInvoke(event);
		
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTimestamp(Instant.now());

		// No argument was given
		if (args.length == 0) {
			builder.setTitle("Incorrect Usage");
			builder.setDescription("Please only provide the id of the user you want to blacklist!");
			sendToOwner(builder);
			return;
		}

		User toBlacklist = getUser(event, args[0]);

		// Couldn't find user (probably because bot doesn't share a server with them)
		if (toBlacklist == null) {
			sendUserNotFoundEmbed();
			return;
		}

		String reason = args.length > 1
				? String.join(" ", Arrays.copyOfRange(args, 1, args.length))
				: "None given";

		try {
			permissionManager.blacklist(toBlacklist, reason, event.getMessage().getTimeCreated().toLocalDateTime().toString());
		} catch (SQLException ex) {
			builder.setTitle("Error");
			builder.setDescription("An error occurred while trying to blacklist this user!");
			sendToOwner(builder);

			if (logger.isErrorEnabled()) {
				logger.error("An error occurred while trying to blacklist the user with id={}.", toBlacklist.getIdLong(), ex);
			}

			return;
		}

		String description = String.format("**Name:** %s\n**Tag:** %s\n**Id:** %s\n**Reason:** %s",
				toBlacklist.getAsMention(), toBlacklist.getAsTag(), toBlacklist.getId(), reason);
		sendSuccessEmbed(description);
	}

	/**
	 * Retrieves a {@link User} from a given String.
	 */
	@Nullable
	private User getUser(MessageReceivedEvent event, String arg) {
		long id;
		try {
			id = Long.parseLong(arg.replace("<@!", "").replace(">", ""));
		} catch (NumberFormatException ex) {
			return null;
		}
		return event.getJDA().getUserById(id);
	}

	/**
	 * Sends an embed stating that the blacklist was successful.
	 */
	private void sendSuccessEmbed(String description) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("User successfully blacklisted");
		builder.setDescription(description);
		builder.setTimestamp(Instant.now());
		sendToOwner(builder);
	}

	/**
	 * Sends an embed stating that it couldn't find the user
	 */
	private void sendUserNotFoundEmbed() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Error");
		builder.setDescription("I couldn't find this user! Make sure to provide a valid user id or mention.");
		builder.setTimestamp(Instant.now());
		sendToOwner(builder);
	}
}