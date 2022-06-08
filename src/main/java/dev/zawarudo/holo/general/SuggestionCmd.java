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

@Command(name = "suggestion",
		description = "Use this command if you want to suggest a feature. Suggestions are always appreciated.",
		usage = "<text>",
		example = "Make this bot more awesome <3",
		category = CommandCategory.GENERAL)
public class SuggestionCmd extends AbstractCommand {

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);
		EmbedBuilder eb = new EmbedBuilder();

		if (args.length == 0) {
			eb.setTitle("Incorrect Usage");
			eb.setDescription("Please provide a description of your suggestion");
			sendEmbed(e, eb, 30, TimeUnit.SECONDS, false);
			return;
		}

		String text = String.join(" ", args);

		try {

			// TODO: Move this block of code to DBOperations class.

			Connection conn = Database.getConnection();
			PreparedStatement ps = conn.prepareStatement("INSERT INTO Submissions (type, user_id, text, date, guild_id, channel_id) VALUES (?, ?, ?, ?, ?, ?);");
			ps.setString(1, "suggestion");
			ps.setString(2, e.getAuthor().getId());
			ps.setString(3, text);
			ps.setString(4, e.getMessage().getTimeCreated().toString());
			ps.setString(5, e.getGuild().getId());
			ps.setString(6, e.getChannel().getId());
			ps.execute();
			ps.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
			eb.setTitle("Error");
			eb.setDescription("An error occurred while connecting to the database!");
			sendEmbed(e, eb, 30, TimeUnit.SECONDS, false);
			return;
		}

		eb.setTitle("Suggestion Submitted");
		eb.setDescription("Thank you for your suggestion!");
		sendEmbed(e, eb, 30, TimeUnit.SECONDS, false);
	}
}