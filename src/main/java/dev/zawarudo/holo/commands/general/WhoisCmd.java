package dev.zawarudo.holo.commands.general;

import dev.zawarudo.holo.utils.annotations.Command;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Command(name = "whois",
		description = "Returns information about a user or bot.",
		usage = "[user]",
		example = "@Holo",
		alias = {"stalk"},
		category = CommandCategory.GENERAL)
public class WhoisCmd extends AbstractCommand {

	@Override
	public void onCommand(@NotNull MessageReceivedEvent e) {
		deleteInvoke(e);

		if (args.length > 1) {
			sendErrorEmbed(e, "Incorrect usage. Please provide at most one argument.");
			return;
		}

		User user = getUser(e);

		if (user == null) {
			sendErrorEmbed(e, "I couldn't find the given user! Please make sure you provided the correct user id or mentioned them!");
			return;
		}

		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("@" + user.getName() + " (" + user.getIdLong() + ")");
		builder.setThumbnail(user.getEffectiveAvatarUrl() + "?size=1024");

		Member member = getMember(e, user);

		// User isn't in guild
		if (member == null) {
			// Not applicable fields
			builder.addField("Nickname", "`N/A`", false);
			builder.addField("Join Date", "`N/A`", false);
			builder.addField("Highest Role", "`N/A`", true);
			builder.addField("Hoisted Role", "`N/A`", true);
			builder.addField("Roles", "`N/A`", false);

			// Account information
			var localDateTime = LocalDateTime.ofInstant(user.getTimeCreated().toInstant(), ZoneId.of("Europe/Zurich"));
			String s = localDateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.SHORT));
			String type = user.isBot() ? "Bot" : "User";
			builder.addField("Additional Checks", "Account Type: `" + type + "`\n" + "Creation Date: `" + s + "`", false);

			sendEmbed(e, builder, true, 5, TimeUnit.MINUTES);
			return;
		}

		builder.setDescription("`" + member.getEffectiveName() + "` " + member.getAsMention());

		var localDateTime = LocalDateTime.ofInstant(member.getTimeJoined().toInstant(), ZoneId.of("Europe/Zurich"));
		String s = localDateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.SHORT));

		builder.addField("Join Date", "`" + s + "`", false);

		Color embedColor = member.getColor();

		List<Role> roles = member.getRoles();

		// Member has no roles in this guild
		if (roles.isEmpty()) {
			builder.addField("Highest Role", "@everyone", true);
			builder.addField("Hoisted Role", "`Unhoisted`", true);
			builder.addField("Roles", "@everyone", false);
		}

		// Member has roles
		else {
			Role highest = roles.get(0);
			Role hoisted = null;

			for (Role role : roles) {
				if (role.getPosition() > highest.getPosition()) {
					highest = role;
				}

				if (hoisted != null || !role.isHoisted()) {
					if (!role.isHoisted() || role.getPosition() <= hoisted.getPosition()) {
						continue;
					}
				}
				hoisted = role;
			}

			builder.addField("Highest Role", highest.getAsMention(), true);

			if (hoisted == null) {
				builder.addField("Hoisted Role", "`Unhoisted`", true);
			} else {
				builder.addField("Hoisted Role", hoisted.getAsMention(), true);
			}

			builder.addBlankField(true);

			StringBuilder rolesString = new StringBuilder(roles.get(0).getAsMention());

			int counter = 1;
			for (int i = 1; i < roles.size(); ++i) {
				if (counter == 10) {
					rolesString.append(", `and ").append(roles.size() - counter).append(" more...`");
					break;
				}
				rolesString.append(", ").append(roles.get(i).getAsMention());
				counter++;
			}
			builder.addField("Roles", rolesString.toString(), false);
		}

		localDateTime = LocalDateTime.ofInstant(user.getTimeCreated().toInstant(), ZoneId.of("Europe/Zurich"));
		s = localDateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.SHORT));
		String type = user.isBot() ? "Bot" : "User";

		builder.addField("Additional Checks", "Account Type: `" + type + "`\n" + "Creation Date: `" + s + "`", false);
		sendEmbed(e, builder, true, 5, TimeUnit.MINUTES, embedColor);
	}

	/**
	 * Returns the given user, either as mention or as id. If the argument is
	 * invalid, simply return the author of the message.
	 */
	private User getUser(MessageReceivedEvent e) {
		if (args.length == 0) {
			return e.getAuthor();
		}
		try {
			long id = Long.parseLong(args[0]
					.replace("<", "")
					.replace(">", "")
					.replace("!", "")
					.replace("@", ""));
			return e.getJDA().getUserById(id);
		} catch (NumberFormatException ex) {
			return e.getAuthor();
		}
	}

	/**
	 * Returns the user as a member of the guild.
	 */
	private Member getMember(MessageReceivedEvent e, User user) {
		try {
			return e.getGuild().retrieveMember(user).complete();
		} catch (ErrorResponseException | IllegalArgumentException ex) {
			return null;
		}
	}
}