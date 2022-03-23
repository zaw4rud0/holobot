package com.xharlock.holo.music.cmds;

import java.util.concurrent.TimeUnit;

import com.xharlock.holo.music.core.GuildMusicManager;
import com.xharlock.holo.music.core.MusicCommand;
import com.xharlock.holo.music.core.PlayerManager;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class LeaveCmd extends MusicCommand {

	public LeaveCmd(String name) {
		super(name);
		setDescription("Use this command to make me leave the VC");
		setUsage(name);
		setIsOwnerCommand(true);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);
		
		EmbedBuilder builder = new EmbedBuilder();

		if (!isBotInAudioChannel(e)) {
			builder.setTitle("Error");
			builder.setDescription("I'm not in any voice channel!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}

		GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());
		musicManager.clear();
		e.getGuild().getAudioManager().closeAudioConnection();

		builder.setTitle("Disconnected");
		builder.setDescription("See you soon!");
		sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
	}
}