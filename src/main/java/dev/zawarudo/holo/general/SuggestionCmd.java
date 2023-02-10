package dev.zawarudo.holo.general;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.database.DBOperations;
import dev.zawarudo.holo.misc.Submission;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

@Command(name = "suggestion",
		description = "Use this command if you want to suggest a feature. Suggestions are always appreciated.",
		usage = "<text>",
		example = "Make this bot more awesome <3",
		category = CommandCategory.GENERAL)
public class SuggestionCmd extends AbstractCommand {

	@Override
	public void onCommand(@NotNull MessageReceivedEvent event) {
		deleteInvoke(event);
		EmbedBuilder eb = new EmbedBuilder();

		if (args.length == 0) {
			eb.setTitle("Incorrect Usage");
			eb.setDescription("Please provide a description of your suggestion");
			sendEmbed(event, eb, false, 30, TimeUnit.SECONDS);
			return;
		}

		try {
			Submission submission = new Submission(Submission.Type.SUGGESTION, event, String.join(" ", args));
			DBOperations.insertSubmission(submission);
		} catch (SQLException ex) {
			eb.setTitle("Error");
			eb.setDescription("An error occurred while connecting to the database!");
			sendEmbed(event, eb, false,30, TimeUnit.SECONDS);
			return;
		}

		eb.setTitle("Suggestion Submitted");
		eb.setDescription("Thank you for your suggestion!");
		sendEmbed(event, eb, false, 30, TimeUnit.SECONDS);
	}
}