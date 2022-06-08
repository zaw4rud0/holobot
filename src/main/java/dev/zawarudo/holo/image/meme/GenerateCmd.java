package dev.zawarudo.holo.image.meme;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.annotations.Deactivated;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.utils.ImageOperations;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Command that generates an image with a given user
 */
@Deactivated
@Command(name = "generate",
		description = "Generates an image with a user in it",
		alias = {"gen", "meme"},
		category = CommandCategory.EXPERIMENTAL)
public class GenerateCmd extends AbstractCommand {

	@Override
	public void onCommand(MessageReceivedEvent e) {
		EmbedBuilder builder = new EmbedBuilder();
		deleteInvoke(e);
		
		// Show all templates
		if (args.length == 0) {
			builder.setTitle("List of Templates");
			builder.setDescription("");
			sendEmbed(e, builder, 1, TimeUnit.MINUTES, true);
			return;
		}
		
		Template temp = null;
		
		for (Template t : Template.values()) {
			if (args[0].toLowerCase(Locale.UK).equals(t.getName())) {
				temp = t;
				break;
			}
		}
		
		if (temp == null) {
			builder.setTitle("Error");
			builder.setDescription("Please choose a template to generate an image from!");
			sendEmbed(e, builder, 30, TimeUnit.SECONDS, false);
			return;
		}
		
		sendTyping(e);
		
		InputStream input;
		
		try {
			BufferedImage result = temp.generate(e.getGuild().getMemberById(args[0]).getEffectiveAvatarUrl() + "?size=256");
			input = ImageOperations.toInputStream(result);
		} catch (IOException ex) {
			ex.printStackTrace();
			return;
		}
		
		e.getChannel().sendFile(input, temp.getName() + ".png").queue();
	}
}