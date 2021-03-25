package com.xharlock.otakusenpai.commands.cmds;

import com.xharlock.otakusenpai.commands.core.Command;
import com.xharlock.otakusenpai.commands.core.CommandCategory;
import com.xharlock.otakusenpai.commands.core.CommandManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class HelpCmd extends Command {
	
	// TODO
	// * Only display allowed commands
	// * Only show aliases if command has those
	// * 
		
	private CommandManager manager;
	
	public HelpCmd(String name, CommandManager manager) {
		super(name);
		setDescription("Use this comamnd to display a list of all commands or to show more informations about a specific command.");
		setUsage("help [command]");
		setExample("help ping");
		setCommandCategory(CommandCategory.GENERAL);
		this.manager = manager;
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		
		EmbedBuilder builder = new EmbedBuilder();
		
		if (args.length == 0) {
			builder.setThumbnail(e.getJDA().getSelfUser().getEffectiveAvatarUrl());
		}
	}
}
