package com.xharlock.otakusenpai.music.cmds;

import java.util.concurrent.TimeUnit;

import com.xharlock.otakusenpai.music.core.*;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class LeaveCmd extends MusicCommand {

	public LeaveCmd(String name) {
		super(name);
		setDescription("Use this command to make me leave the VC");
		setUsage(name);
		setIsOwnerCommand(true);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		e.getMessage().delete().queue();
		
		e.getChannel().sendTyping().queue();
		
		EmbedBuilder builder = new EmbedBuilder();

		if (!isBotInChannel(e)) {
			builder.setTitle("Error");
			builder.setDescription("I'm not in any voice channel!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
			return;
		}

		GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());

		musicManager.scheduler.looping = false;
		musicManager.scheduler.queue.clear();
		musicManager.audioPlayer.stopTrack();

		AudioManager audioManager = e.getGuild().getAudioManager();
		audioManager.closeAudioConnection();

		builder.setTitle("Disconnected");
		builder.setDescription("See you soon!");
		sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
	}

}
