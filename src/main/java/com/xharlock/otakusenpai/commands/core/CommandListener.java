package com.xharlock.otakusenpai.commands.core;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.regex.Pattern;

import com.xharlock.otakusenpai.core.Main;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter {

	private CommandManager commandManager;

	public CommandListener(CommandManager commandManager) {
		this.commandManager = commandManager;
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent e) {

		if (e.isWebhookMessage())
			return;

		// TODO Implement BotHandler
		if (e.getAuthor().isBot())
			return;

		String prefix;
		
		if (e.isFromGuild())
			prefix = getGuildPrefix(e.getGuild());
		else
			prefix = Main.otakuSenpai.getConfig().getPrefix();
		
		if (!e.getMessage().getContentRaw().startsWith(prefix))
			return;

		String[] split = e.getMessage().getContentRaw().replaceFirst("(?i)" + Pattern.quote(prefix), "").split("\\s+");
		String invoke = split[0].toLowerCase();

		if (this.commandManager.getCommand(invoke) == null)
			return;

		Command cmd = this.commandManager.getCommand(invoke);

		// Check if user can do anything
		if (!Main.otakuSenpai.getPermission().check(e, cmd))
			return;

		cmd.args = Arrays.copyOfRange(split, 1, split.length);
		System.out.println(String.valueOf(LocalDateTime.now().toString()) + " : " + e.getAuthor() + " has called "
				+ cmd.getName());
		cmd.onCommand(e);
	}

	private String getGuildPrefix(Guild guild) {
		return Main.otakuSenpai.getGuildConfigManager().getGuildConfig(guild).getGuildPrefix();
	}
}
