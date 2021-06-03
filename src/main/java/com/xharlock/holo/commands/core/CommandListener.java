package com.xharlock.holo.commands.core;

import java.util.Arrays;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xharlock.holo.core.Bootstrap;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter {

	private CommandManager commandManager;

	private static final Logger logger = LoggerFactory.getLogger(CommandListener.class);
	
	public CommandListener(CommandManager commandManager) {
		this.commandManager = commandManager;
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		// Ignore webhooks and bots
		if (e.isWebhookMessage() || e.getAuthor().isBot()) return;
		
		if (!e.getMessage().getContentRaw().startsWith(getPrefix(e)))
			return;

		String[] split = e.getMessage().getContentRaw().replaceFirst("(?i)" + Pattern.quote(getPrefix(e)), "").split("\\s+");
		String invoke = split[0].toLowerCase();

		if (commandManager.getCommand(invoke) == null)
			return;

		Command cmd = commandManager.getCommand(invoke);

		// Check if user can do anything
		if (!Bootstrap.holo.getPermissionManager().check(e, cmd))
			return;

		cmd.args = Arrays.copyOfRange(split, 1, split.length);
		cmd.onCommand(e);

		logger.info(e.getAuthor() + " has called " + cmd.getName());
	}

	private String getPrefix(MessageReceivedEvent e) {
		if (e.isFromGuild())
			return Bootstrap.holo.getGuildConfigManager().getGuildConfig(e.getGuild()).getGuildPrefix();
		else
			return Bootstrap.holo.getConfig().getPrefix();
	}
}
