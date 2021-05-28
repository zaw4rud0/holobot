package com.xharlock.holo.commands.cmds;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ServerInfoCmd extends Command {

	public ServerInfoCmd(String name) {
		super(name);
		setDescription("Use this command to display all informations regarding this server.");
		setUsage(name);
		setIsGuildOnlyCommand(true);
		setCommandCategory(CommandCategory.GENERAL);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		e.getMessage().delete().queue();
		e.getChannel().sendTyping().queue();
		
		EmbedBuilder builder = new EmbedBuilder();
		
		builder.setTitle(e.getGuild().getName() + " (" + e.getGuild().getId() + ")");
		
		if (e.getGuild().getIconUrl() != null)
			builder.setThumbnail(e.getGuild().getIconUrl());
		
		builder.addField("Owner", e.getGuild().getOwner().getAsMention(), true);
		builder.addField("Region", e.getGuild().getRegion().getName(), true);
		builder.addField("Members", "" + e.getGuild().getMemberCount(), true);
		
		builder.addField("Creation Date", "`" + e.getGuild().getTimeCreated().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.SHORT)) + "`", false);
		
		List<Emote> normal = new ArrayList<>();
		List<Emote> animated = new ArrayList<>();
		
		for (Emote em : e.getGuild().getEmotes()) {
			if (em.isAnimated())
				animated.add(em);
			else
				normal.add(em);
		}
		
		builder.addField("Additional Checks",
				"Normal Emotes: `" + normal.size() + " / " + e.getGuild().getMaxEmotes() + "`\n"
				+ "Animated Emotes: `" + animated.size() + " / " + e.getGuild().getMaxEmotes() + "`\n"
				+ "Channels: `" + e.getGuild().getChannels().size() + " / 500`\n"
				+ "Roles: `" + e.getGuild().getRoles().size() + " / 250`",
				false);
		
		if (e.getGuild().getSplashUrl() != null)
			builder.setImage(e.getGuild().getSplashUrl().replace(".png", ".webp") + "?size=4096");	
		
		sendEmbed(e, builder, 1, TimeUnit.MINUTES, true);
	}

}
