package dev.zawarudo.holo.commands;

import dev.zawarudo.holo.commands.image.ActionCmd;
import dev.zawarudo.holo.core.Bootstrap;
import dev.zawarudo.holo.core.PermissionManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Class that listens to messages and checks if a bot command has been called. If that's
 * the case, it executes the command with the given arguments.
 */
public class CommandListener extends ListenerAdapter {

	private final CommandManager cmdManager;
	private final PermissionManager permManager;

	private static final Logger LOGGER = LoggerFactory.getLogger(CommandListener.class);

	public CommandListener(CommandManager cmdManager, PermissionManager permManager) {
		this.cmdManager = cmdManager;
		this.permManager = permManager;
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		// Ignore webhooks and bots
		if (e.isWebhookMessage() || e.getAuthor().isBot()) {
			return;
		}

		// Ignore messages without the set prefix
		if (!e.getMessage().getContentRaw().startsWith(getPrefix(e))) {
			return;
		}

		String[] split = e.getMessage().getContentRaw().replaceFirst("(?i)" + Pattern.quote(getPrefix(e)), "").split("\\s+");
		String invoke = split[0].toLowerCase(Locale.UK);

		// Action cmd has been called
		ActionCmd actionCmd = (ActionCmd) cmdManager.getCommand("action");
		if (actionCmd.isAction(invoke)) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("{} has called action ({})", e.getAuthor(), invoke);
			}

			actionCmd.args = Arrays.copyOfRange(split, 1, split.length);
			actionCmd.displayAction(e, actionCmd.getAction(invoke));
		}

		// No valid command
		if (!cmdManager.isValidName(invoke)) {
			return;
		}

		AbstractCommand cmd = cmdManager.getCommand(invoke);

		// Check if user can do anything
		if (!permManager.hasUserPermission(e, cmd) || !permManager.hasChannelPermission(e, cmd)) {
			return;
		}

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("{} has called {}", e.getAuthor(), cmd.getName());
		}

		cmd.args = Arrays.copyOfRange(split, 1, split.length);
		cmd.onCommand(e);
	}

	/**
	 * Retrieves the prefix for the given guild or the default prefix if the event is from a DM Channel.
	 *
	 * @param e The {@link MessageReceivedEvent} to get the prefix for.
	 * @return The prefix of the bot.
	 */
	private String getPrefix(MessageReceivedEvent e) {
		if (e.isFromGuild()) {
			return Bootstrap.holo.getGuildConfigManager().getGuildConfig(e.getGuild()).getPrefix();
		} else {
			return Bootstrap.holo.getConfig().getDefaultPrefix();
		}
	}
}