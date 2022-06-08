package dev.zawarudo.holo.general;

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

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Command(name = "8ball",
		description = "Ask the magic 8 ball a question",
		usage = "<question>",
		thumbnail = "https://media.discordapp.net/attachments/778991087847079972/946790101109841990/magic8ball.png",
		guildOnly = false,
		category = CommandCategory.MISC)
public class Magic8BallCmd extends AbstractCommand {

	private static final String apiUrl = "https://nekos.life/api/v2/8ball";

	@Override
	public void onCommand(MessageReceivedEvent e) {
		sendTyping(e);
		EmbedBuilder builder = new EmbedBuilder();
		
		if (args.length == 0) {
			deleteInvoke(e);
			builder.setTitle("Incorrect usage");
			builder.setDescription("Please ask a question");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, true);
			return;
		}
		
		Answer answer = getAnswer();
		
		if (answer == null) {
			deleteInvoke(e);
			builder.setTitle("Error");
			builder.setDescription("Something went wrong while fetching an answer. Please try again in a few minutes!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, true);			
			return;
		}
		
		builder.setTitle("Magic 8-Ball");
		builder.setImage(answer.url);
		
		Message msg = e.getMessage().replyEmbeds(builder.build()).complete();
		
		while (answer.response.toLowerCase(Locale.UK).equals("wait for it")) {
			answer = getAnswer();
			builder.setTitle("Magic 8-Ball");
			builder.setImage(answer.url);
			msg = msg.replyEmbeds(builder.build()).completeAfter(10, TimeUnit.SECONDS);
		}
	}
	
	private Answer getAnswer() {
		JsonObject obj = null;
		try {
			obj = HttpResponse.getJsonObject(apiUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Gson().fromJson(obj, Answer.class);
	}
	
	private static class Answer {
		@SerializedName("response")
		String response;
		@SerializedName("url")
		String url;
	}
}