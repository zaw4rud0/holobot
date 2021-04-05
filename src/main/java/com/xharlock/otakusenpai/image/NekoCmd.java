package com.xharlock.otakusenpai.image;

import java.util.List;

import com.xharlock.otakusenpai.commands.core.Command;
import com.xharlock.otakusenpai.commands.core.CommandCategory;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class NekoCmd extends Command {

	private String[] URLs = { "https://nekos.life/api/v2/img/neko", "https://neko-love.xyz/api/v1/neko",
			"https://nekos.life/api/v2/img/kemonomimi", "http://api.nekos.fun:8080/api/neko" };

	public NekoCmd(String name) {
		super(name);
		setDescription("Use this command to get a picture of a catgirl (neko)");
		setAliases(List.of("catgirl", "kemonomimi"));
		setUsage(name);
		setIsGuildOnlyCommand(false);
		setCommandCategory(CommandCategory.IMAGE);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {

	}
}
