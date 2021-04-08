package com.xharlock.otakusenpai.commands.cmds;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.xharlock.otakusenpai.commands.core.Command;
import com.xharlock.otakusenpai.commands.core.CommandCategory;
import com.xharlock.otakusenpai.core.Main;
import com.xharlock.otakusenpai.misc.Messages;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class WhoisCmd extends Command {

	public WhoisCmd(String name) {
		super(name);
		setDescription("Use this command to get more informations about a given user or bot");
		setUsage(name + " [user or bot]");
		setExample(name + " " + Main.otakuSenpai.getJDA().getSelfUser().getAsMention());
		setAliases(List.of("stalk"));
		setIsGuildOnlyCommand(true);
		setCommandCategory(CommandCategory.GENERAL);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		e.getMessage().delete().queue();
		EmbedBuilder builder = new EmbedBuilder();

		if (args.length > 1) {
			builder.setTitle(Messages.TITLE_INCORRECT_USAGE.getText());
			builder.setDescription("Please only provide one argument");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}

		User user = e.getAuthor();

		try {
			user = e.getGuild().getMemberById(args[0].replace("<@!", "").replace(">", "")).getUser();
		} catch (Exception ex) {
		}

		Member member = e.getGuild().retrieveMember(user).complete();

		builder.setTitle("@" + user.getAsTag() + " (" + user.getIdLong() + ")");
		builder.setThumbnail(user.getEffectiveAvatarUrl());
		builder.addField("Nickname", "`" + member.getEffectiveName() + "`", false);

		OffsetDateTime d = member.getTimeJoined();
		String s = d.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.SHORT));
		builder.addField("Join Date", "`" + s + "`", false);

		List<Role> roles = member.getRoles();

		// Member has no roles in this guild
		if (roles.size() == 0) {
			builder.addField("Highest Role", "@everyone", true);
			builder.addField("Hoisted Role", "`Unhoisted`", true);
			builder.addField("Roles", "@everyone", false);
		} 
		// Member has roles
		else {
			Role highest = roles.get(0);
			Role hoisted = null;

			for (Role role : roles) {
				if (role.getPosition() > highest.getPosition())
					highest = role;
				if (hoisted == null && role.isHoisted())
					hoisted = role;
				else {
					if (!role.isHoisted() || role.getPosition() <= hoisted.getPosition())
						continue;
					hoisted = role;
				}
			}

			builder.addField("Highest Role", highest.getAsMention(), true);
			
			if (hoisted == null)
				builder.addField("Hoisted Role", "`Unhoisted`", true);
			else
				builder.addField("Hoisted Role", hoisted.getAsMention(), true);

			String rolesString = roles.get(0).getAsMention();

			int counter = 1;

			for (int i = 1; i < roles.size(); ++i) {
				if (counter == 10) {
					rolesString = rolesString + ", `and " + (roles.size() - counter) + " more...`";
					break;
				}
				rolesString = rolesString + ", " + roles.get(i).getAsMention();
				counter++;
			}

			builder.addField("Roles", rolesString, false);

		}

		d = user.getTimeCreated();
		s = d.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.SHORT));
		builder.addField("Account Creation Date", "`" + s + "`", false);

		if (user.isBot())
			builder.addField("Account Type", "Bot", true);
		else
			builder.addField("Account Type", "User", true);

		this.sendEmbed(e, builder, true);
	}

}
