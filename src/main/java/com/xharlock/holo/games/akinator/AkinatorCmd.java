package com.xharlock.holo.games.akinator;

import com.markozajc.akiwrapper.core.entities.Server.GuessType;
import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.annotations.Deactivated;
import com.xharlock.holo.core.AbstractCommand;
import com.xharlock.holo.core.CommandCategory;
import com.xharlock.holo.core.Bootstrap;
import com.xharlock.holo.exceptions.APIException;
import com.xharlock.holo.misc.EmbedColor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Deactivated
@Command(name = "akinator2",
		description = """
			Play the Akinator game.

			In a nutshell, you have to choose a guess type and then choose a member of that type, real or fictional, and answer my questions by using the according reaction. Possible answers are `Yes`, `No`, `I don't know`, `Probably` and `Probably not`. After some questions I will try to guess your chosen object.

			Possible guess types:
			```-Animals => animal
			-TV Shows and Movies => movie
			-Characters => character
			-Objects => object```\t
			""",
		usage = "[<guess type>]",
		embedColor = EmbedColor.AKINATOR,
		thumbnail = AkinatorSprite.DEFAULT,
		category = CommandCategory.GAMES)
public class AkinatorCmd extends AbstractCommand {

	private final AkinatorManager manager;
	
	public AkinatorCmd() {
		manager = Bootstrap.holo.getAkinatorManager();
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		EmbedBuilder builder = new EmbedBuilder();
		
		// Check if user is already playing
		if (manager.hasInstance(e.getAuthor().getIdLong())) {
			e.getMessage().reply("You are already playing this game. Please finish or cancel it before you start a new game!").queue();
			return;
		}
		
		GuessType type = GuessType.CHARACTER;
		
		if (args.length != 0) {
			type = getGuessType(String.join(" ", args));
			
			if (type == null) {
				builder.setTitle("Unknown Guess Type");
				builder.setDescription("Use `" + getPrefix(e) + "help akinator` to see all available guess types.");
				sendEmbed(e, builder, 30, TimeUnit.SECONDS, false, getEmbedColor());
				return;
			}
		}
		sendTyping(e);
		
		Akinator akinator;
		
		try {
			akinator = manager.createInstance(e, type);
		} catch (APIException ex) {
			builder.setTitle("Error");
			builder.setDescription("Something went wrong. Please try again in a few minutes!");
			sendEmbed(e, builder, 30, TimeUnit.SECONDS, false);
			return;
		}

		akinator.start();
	}

	/**
	 * Gets the guess type from the given string.
	 */
	@Nullable
	private GuessType getGuessType(@NotNull String s) {
		return switch (s.toLowerCase(Locale.UK)) {
			case ("animal") -> GuessType.ANIMAL;
			case ("movie") -> GuessType.MOVIE_TV_SHOW;
			case ("object") -> GuessType.OBJECT;
			case ("character") -> GuessType.CHARACTER;
			default -> null;
		};
	}
}