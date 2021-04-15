package com.xharlock.otakusenpai.image;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.xharlock.otakusenpai.commands.core.Command;
import com.xharlock.otakusenpai.commands.core.CommandCategory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class UpscaleCmd extends Command {

	public UpscaleCmd(String name) {
		super(name);
		setDescription("Use this command to upscale an image. You can also use an image as attachment or a link to it.");
		setUsage(name + " [image link]");
		setCommandCategory(CommandCategory.IMAGE);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		
		if (e.isFromGuild())
			e.getMessage().delete().queue();
		
		EmbedBuilder builder = new EmbedBuilder();
		String oldUrl = "";

		if (args.length == 0) {
			if (e.getMessage().getAttachments().size() == 0) {
				builder.setTitle("Incorrect Usage");
				builder.setDescription("You need to provide an image either as an attachment or as a link!");
				sendEmbed(e, builder, 15L, TimeUnit.SECONDS, false);
				return;
			}
			oldUrl = e.getMessage().getAttachments().get(0).getUrl();
		}

		else {
			if (args.length != 1) {
				builder.setTitle("Incorrect Usage");
				builder.setDescription("You need to provide an image either as an attachment or as a link!");
				sendEmbed(e, builder, 15L, TimeUnit.SECONDS, false);
				return;
			}

			oldUrl = args[0].replace("<", "").replace(">", "");
		}

		String imgUrl = "";

		try {
			imgUrl = Waifu2xWrapper.upscaleImage(oldUrl);
		} catch (IOException ex) {
			builder.setTitle("Error");
			builder.setDescription("Something went wrong while communicating with the API");
			sendEmbed(e, builder, 15L, TimeUnit.SECONDS, false);
			return;
		}
		
		builder.setTitle("Upscaled Image");
		builder.setImage(imgUrl);
		sendEmbed(e, builder, 2L, TimeUnit.MINUTES, true);
	}

}
