package dev.zawarudo.holo.commands.owner;

import dev.zawarudo.holo.core.security.BlacklistService;
import dev.zawarudo.holo.utils.annotations.CommandInfo;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Arrays;

/**
 * Command to blacklist a user from using the bot.
 */
@CommandInfo(name = "blacklist",
		description = "Blacklists an user from using the bot.",
		usage = "<user id|@mention> [reason...] | remove <user id|@mention>",
		ownerOnly = true,
		category = CommandCategory.OWNER)
public class BlacklistCmd extends AbstractCommand {

	private final BlacklistService blacklistService;

	public BlacklistCmd(BlacklistService blacklistService) {
		this.blacklistService = blacklistService;
	}

	@Override
	public void onCommand(@NotNull MessageReceivedEvent event) {
		deleteInvoke(event);

		if (args.length == 0) {
			sendUsage(event, "Missing arguments.");
			return;
		}

		if (isRemoveMode(args[0])) {
			handleRemove(event);
			return;
		}

		handleAdd(event);
	}

	private void handleAdd(@NotNull MessageReceivedEvent event) {
		Long userId = parseUserId(args[0]);
		if (userId == null) {
			sendUsage(event, "Invalid user id / mention: `" + args[0] + "`");
			return;
		}

		String reason = parseReasonFromIndex(1);

		try {
			blacklistService.blacklist(
					userId,
					reason,
					event.getMessage().getTimeCreated().toString()
			);
		} catch (SQLException ex) {
			logger.error("Failed to blacklist userId={}", userId, ex);
			sendErrorToOwner("Database error while blacklisting user.", ex);
			return;
		}

		User cached = event.getJDA().getUserById(userId);
		String who = formatUser(cached, userId);

		sendToOwner(embed()
				.setTitle("User successfully blacklisted")
				.setDescription("**User:** " + who + "\n**Reason:** " + reason));
	}


	private void handleRemove(@NotNull MessageReceivedEvent event) {
		if (args.length < 2) {
			sendUsage(event, "Missing user id / mention for remove.");
			return;
		}

		Long userId = parseUserId(args[1]);
		if (userId == null) {
			sendUsage(event, "Invalid user id / mention: `" + args[1] + "`");
			return;
		}

		try {
			blacklistService.unblacklist(userId);
		} catch (SQLException ex) {
			logger.error("Failed to unblacklist userId={}", userId, ex);
			sendErrorToOwner("Database error while removing user from blacklist.", ex);
			return;
		}

		User cached = event.getJDA().getUserById(userId);
		String who = formatUser(cached, userId);

		sendToOwner(embed()
				.setTitle("User removed from blacklist")
				.setDescription("**User:** " + who));
	}

	private boolean isRemoveMode(String firstArg) {
		String s = firstArg.toLowerCase();
		return s.equals("remove") || s.equals("unblacklist") || s.equals("delete");
	}

	private Long parseUserId(String raw) {
		String s = raw.trim()
				.replace("<@!", "")
				.replace("<@", "")
				.replace(">", "");

		try {
			return Long.parseLong(s);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private String parseReasonFromIndex(int index) {
		if (args.length <= index) return "None given";
		String r = String.join(" ", Arrays.copyOfRange(args, index, args.length)).trim();
		return r.isBlank() ? "None given" : r;
	}

	private String formatUser(User cached, long userId) {
		if (cached == null) return "`" + userId + "`";
		return cached.getAsMention() + " (`" + cached.getName() + "`, `" + cached.getId() + "`)";
	}

	private void sendUsage(@NotNull MessageReceivedEvent event, String message) {
		String p = getPrefix(event);
		sendToOwner(embed()
				.setTitle("Incorrect Usage")
				.setDescription(message + "\n\n" +
						"Add: `" + p + "blacklist <userId|@mention> [reason...]`\n" +
						"Remove: `" + p + "blacklist remove <userId|@mention>`"));
	}

	private void sendErrorToOwner(String message, Exception ex) {
		sendToOwner(embed()
				.setTitle("Error")
				.setDescription(message));
	}

	private EmbedBuilder embed() {
		return new EmbedBuilder().setTimestamp(Instant.now());
	}
}