package com.xharlock.holo.core;

import com.xharlock.holo.image.ActionCmd;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;

public class CommandListener extends ListenerAdapter {

	private final CommandManager manager;
	private static final Logger logger = LoggerFactory.getLogger(CommandListener.class);

	public CommandListener(CommandManager manager) {
		this.manager = manager;
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

		if (!manager.isValidName(invoke)) {
			// Check if it's an action
			ActionCmd actionCmd = (ActionCmd) manager.getCommand("action");
			if (actionCmd.isAction(invoke)) {
				actionCmd.args = Arrays.copyOfRange(split, 1, split.length);
				actionCmd.displayAction(e, actionCmd.getAction(invoke));
				
				if (logger.isInfoEnabled()) {
					logger.info(e.getAuthor() + " has called action");
				}
			}
			return;
		}

		AbstractCommand cmd = manager.getCommand(invoke);

		// Check if user can do anything
		if (!Bootstrap.holo.getPermissionManager().check(e, cmd)) {
			return;
		}

		if (logger.isInfoEnabled()) {
			logger.info(e.getAuthor() + " has called " + cmd.getName());
		}

		cmd.args = Arrays.copyOfRange(split, 1, split.length);
		cmd.onCommand(e);
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
		e.reply("This command is still in WIP and thus not available yet.").setEphemeral(true).queue();
	}

	private String getPrefix(MessageReceivedEvent e) {
		if (e.isFromGuild()) {
			return Bootstrap.holo.getGuildConfigManager().getGuildConfig(e.getGuild()).getPrefix();
		} else {
			return Bootstrap.holo.getConfig().getDefaultPrefix();
		}
	}
}