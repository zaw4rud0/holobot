package com.xharlock.otakusenpai.commands.core;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import com.xharlock.otakusenpai.core.Bootstrap;
import com.xharlock.otakusenpai.misc.ChatClient;
import com.xharlock.otakusenpai.place.Place;

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

		if (!e.getAuthor().equals(e.getJDA().getSelfUser())) {
			ChatClient.doStuff(e);
		}
		
		// TODO Rework
		if (e.getMessage().getContentRaw().equals("--getplace")) {
			try {
				ImageIO.write(Place.getCanvas(), "png", new File("C:/Users/adria/Desktop/place.png"));
				System.out.println("Printing place");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return;
		}
		
		// Place stuff
		if (e.getMessage().getContentRaw().toLowerCase().startsWith(".place")) {
			Bootstrap.otakuSenpai.getPlace().doStuff(e);
			return;
		}
		
		if (e.isWebhookMessage())
			return;

		if (e.getAuthor().isBot())
			return;

		String prefix;
		
		if (e.isFromGuild())
			prefix = getGuildPrefix(e.getGuild());
		else
			prefix = Bootstrap.otakuSenpai.getConfig().getPrefix();
		
		if (!e.getMessage().getContentRaw().startsWith(prefix))
			return;

		String[] split = e.getMessage().getContentRaw().replaceFirst("(?i)" + Pattern.quote(prefix), "").split("\\s+");
		String invoke = split[0].toLowerCase();

		if (this.commandManager.getCommand(invoke) == null)
			return;

		Command cmd = this.commandManager.getCommand(invoke);

		// Check if user can do anything
		if (!Bootstrap.otakuSenpai.getPermission().check(e, cmd))
			return;

		cmd.args = Arrays.copyOfRange(split, 1, split.length);
		System.out.println(String.valueOf(LocalDateTime.now().toString()) + " : " + e.getAuthor() + " has called "
				+ cmd.getName());
		cmd.onCommand(e);
	}

	private String getGuildPrefix(Guild guild) {
		return Bootstrap.otakuSenpai.getGuildConfigManager().getGuildConfig(guild).getGuildPrefix();
	}
}
