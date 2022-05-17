package com.xharlock.holo.general;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.core.AbstractCommand;
import com.xharlock.holo.core.Bootstrap;
import com.xharlock.holo.core.CommandCategory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Command(name = "serverinfo",
		description = "Shows information about the server",
		category = CommandCategory.GENERAL)
public class ServerInfoCmd extends AbstractCommand {

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);

		// Prepare the fields
		String creationDate = "`" + e.getGuild().getTimeCreated().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.SHORT)) + "`";
		long normalCount = e.getGuild().getEmotes().stream().filter(em -> !em.isAnimated()).count();
		long animatedCount = e.getGuild().getEmotes().stream().filter(Emote::isAnimated).count();

		int stickerCount, maxStickers;
		try {
			stickerCount = getStickerCount(e.getGuild());
		} catch (IOException ex) {
			ex.printStackTrace();
			EmbedBuilder builder = new EmbedBuilder();
			builder.setTitle("Error").setDescription("An error occurred while fetching the sticker count.");
			sendEmbed(e, builder, 30, TimeUnit.SECONDS, false, Color.RED);
			return;
		}
		switch (e.getGuild().getBoostTier().getKey()){
			case 1 -> maxStickers = 15;
			case 2 -> maxStickers = 30;
			case 3 -> maxStickers = 60;
			default -> maxStickers = 0;
		}

		String additionalChecks = "Normal Emotes: `" + normalCount + " / " + e.getGuild().getMaxEmotes() + "`\n" 
								+ "Animated Emotes: `" + animatedCount + " / " + e.getGuild().getMaxEmotes() + "`\n"
								+ "Stickers: `" + stickerCount + " / " + maxStickers + "`\n"
								+ "Channels: `" + e.getGuild().getChannels().size() + " / 500`\n" 
								+ "Roles: `" + e.getGuild().getRoles().size() + " / 250`";

		// Set the embed
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle(e.getGuild().getName() + " (" + e.getGuild().getId() + ")");
		if (e.getGuild().getIconUrl() != null) {
			builder.setThumbnail(e.getGuild().getIconUrl());
		}
		builder.addField("Owner", e.getGuild().retrieveOwner().complete().getAsMention(), true);
		builder.addField("Members", "" + e.getGuild().getMemberCount(), true);
		builder.addField("Creation Date", creationDate, false);
		builder.addField("Additional Checks", additionalChecks, false);
		if (e.getGuild().getSplashUrl() != null) {
			builder.setImage(e.getGuild().getSplashUrl().replace(".png", ".webp") + "?size=4096");
		}

		sendEmbed(e, builder, 1, TimeUnit.MINUTES, true);
	}

	/** Returns the sticker count of the guild */
	private static int getStickerCount(Guild g) throws IOException {
		String url = "https://discord.com/api/guilds/" + g.getIdLong() + "/stickers";
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Authorization", "Bot " + Bootstrap.holo.getConfig().getBotToken());
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String s = reader.lines().collect(Collectors.joining("\n"));
		JsonArray array = JsonParser.parseString(s).getAsJsonArray();
		return array.size();
	}
}