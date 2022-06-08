package dev.zawarudo.holo.general;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.database.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

@Command(name = "bug",
		description = "Use this command to report a bug. Please provide a description of the bug and how it happened",
		usage = "<text>",
		example = "Something went wrong",
		category = CommandCategory.GENERAL)
public class BugCmd extends AbstractCommand {

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);
		EmbedBuilder eb = new EmbedBuilder();

		if (args.length == 0) {
			eb.setTitle("Incorrect Usage");
			eb.setDescription("Please provide a description of the bug");
			sendEmbed(e, eb, 30, TimeUnit.SECONDS, false);
			return;
		}

		try {
			Connection conn = Database.getConnection();
			PreparedStatement ps = conn.prepareStatement("INSERT INTO Submissions (type, user_id, text, date, guild_id, channel_id) VALUES (?, ?, ?, ?, ?, ?);");
			ps.setString(1, "bug report");
			ps.setString(2, e.getAuthor().getId());
			ps.setString(3, String.join(" ", args));
			ps.setString(4, e.getMessage().getTimeCreated().toString());
			ps.setString(5, e.getGuild().getId());
			ps.setString(6, e.getChannel().getId());
			ps.execute();
			ps.close();
		} catch (SQLException ex) {
			eb.setTitle("Error");
			eb.setDescription("An error occurred while connecting to the database!");
			sendEmbed(e, eb, 30, TimeUnit.SECONDS, false);
			return;
		}

		eb.setTitle("Bug Report Submitted");
		eb.setDescription("Thank you for reporting this bug! We will work on it as soon as possible.");
		sendEmbed(e, eb, 30, TimeUnit.SECONDS, false);
	}
}