package com.xharlock.otakusenpai.commands.core;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.regex.Pattern;

import com.xharlock.otakusenpai.core.Bootstrap;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter {

	private CommandManager commandManager;

	public CommandListener(CommandManager commandManager) {
		this.commandManager = commandManager;
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		
		// TODO Clean up
		
		if (e.isWebhookMessage())
			return;

		if (e.getAuthor().isBot())
			return;

		String prefix = getPrefix(e);
		if (!e.getMessage().getContentRaw().startsWith(prefix))
			return;

		String[] split = e.getMessage().getContentRaw().replaceFirst("(?i)" + Pattern.quote(prefix), "").split("\\s+");
		String invoke = split[0].toLowerCase();

		if (this.commandManager.getCommand(invoke) == null)
			return;

		Command cmd = this.commandManager.getCommand(invoke);

		// Check if user can do anything
		if (!Bootstrap.otakuSenpai.getPermissionManager().check(e, cmd))
			return;

		cmd.args = Arrays.copyOfRange(split, 1, split.length);
		cmd.onCommand(e);
		
		// TODO Replace with proper logger
		System.out.println(String.valueOf(LocalDateTime.now().toString()) + " : " + e.getAuthor() + " has called " + cmd.getName());	
	}

	private String getPrefix(MessageReceivedEvent e) {
		if (e.isFromGuild())
			return Bootstrap.otakuSenpai.getGuildConfigManager().getGuildConfig(e.getGuild()).getGuildPrefix();
		else
			return Bootstrap.otakuSenpai.getConfig().getPrefix();
	}
}
