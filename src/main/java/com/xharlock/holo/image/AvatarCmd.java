package com.xharlock.holo.image;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class AvatarCmd extends Command {

	public AvatarCmd(String name) {
		super(name);
		setDescription("Use this command to get your own avatar or the avatar of a given user inside the guild. Tip: Use the id of the user if you don't want to ping them.");
		setUsage(name + " [user id]");
		setAliases(List.of("av"));
		setIsGuildOnlyCommand(true);
		setCommandCategory(CommandCategory.IMAGE);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		e.getMessage().delete().queue();

		EmbedBuilder builder = new EmbedBuilder();

		if (args.length > 1) {
			builder.setTitle("Incorrect Usage");
			builder.setDescription("Please only provide at most one argument");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}

		User user = e.getAuthor();
		try {
			user = e.getJDA().getUserById(Long.parseLong(args[0].replace("<@!", "").replace(">", "")));
		} catch (Exception ex) {}
		Member member = e.getGuild().retrieveMember(user).complete();

		String url = user.getEffectiveAvatarUrl() + "?size=512";

		builder.setTitle(member.getEffectiveName() + "'s Avatar", url);
		builder.setImage(url);
		sendEmbed(e, builder, 5, TimeUnit.MINUTES, true);
	}
}
