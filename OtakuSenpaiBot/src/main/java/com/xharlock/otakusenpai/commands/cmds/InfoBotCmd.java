package com.xharlock.otakusenpai.commands.cmds;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.xharlock.otakusenpai.commands.core.Command;
import com.xharlock.otakusenpai.commands.core.CommandCategory;
import com.xharlock.otakusenpai.core.Main;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class InfoBotCmd extends Command {

	public InfoBotCmd(String name) {
		super(name);
		setDescription("Use this command to display informations about me.");
		setUsage(name);
		setAliases(List.of("source", "bot", "sauce"));
		setCommandCategory(CommandCategory.GENERAL);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		e.getMessage().delete().queue();
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle(e.getJDA().getSelfUser().getName() + " | Informations");
		builder.setThumbnail(e.getJDA().getSelfUser().getEffectiveAvatarUrl());
		builder.setDescription("Your senpai for anything :heart:"
				+ "\nUse `" + getGuildPrefix(e.getGuild()) + "help` to see all commands");
		builder.addField("Version", Main.otakuSenpai.getConfig().getVersion(), false);
		builder.addField("Source", "No link yet", false);		// TODO Add source link
		sendEmbed(e, builder, 1, TimeUnit.MINUTES, false);
	}
}
