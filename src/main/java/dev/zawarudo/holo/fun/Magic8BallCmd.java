package dev.zawarudo.holo.fun;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.pokeapi4java.utils.HttpResponse;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Command(name = "8ball",
		description = "Ask the Magic 8 Ball a question and get an answer.",
		usage = "<question>",
		thumbnail = "https://media.discordapp.net/attachments/778991087847079972/946790101109841990/magic8ball.png",
		guildOnly = false,
		category = CommandCategory.MISC)
public class Magic8BallCmd extends AbstractCommand {

	private static final String API_URL = "https://nekos.life/api/v2/8ball";

	@Override
	public void onCommand(@NotNull MessageReceivedEvent e) {
		sendTyping(e);
		EmbedBuilder builder = new EmbedBuilder();
		
		if (args.length == 0) {
			sendErrorEmbed(e, "Incorrect usage of the command. Please ask a question.");
			return;
		}

		Answer answer;
		try {
			answer = getAnswer();
		} catch (IOException ex) {
			sendErrorEmbed(e, "An error occurred while fetching an answer. Please try again later.");
			logError("Magic8Ball: Something went wrong while getting an answer" + ex.getMessage());
			return;
		}
		
		builder.setTitle("Magic 8-Ball");
		builder.setImage(answer.url);
		
		Message msg = e.getMessage().replyEmbeds(builder.build()).complete();
		
		while (answer.response.toLowerCase(Locale.UK).equals("wait for it")) {
			try {
				answer = getAnswer();
			} catch (IOException ex) {
				sendErrorEmbed(e, "An error occurred while fetching an answer. Please try again later.");
				logError("Magic8Ball: Something went wrong while getting an answer" + ex.getMessage());
				return;
			}
			builder.setTitle("Magic 8-Ball");
			builder.setImage(answer.url);
			msg = msg.replyEmbeds(builder.build()).completeAfter(10, TimeUnit.SECONDS);
		}
	}
	
	private Answer getAnswer() throws IOException {
		JsonObject obj = HttpResponse.getJsonObject(API_URL);
		return new Gson().fromJson(obj, Answer.class);
	}
	
	private static class Answer {
		@SerializedName("response")
		String response;
		@SerializedName("url")
		String url;
	}
}