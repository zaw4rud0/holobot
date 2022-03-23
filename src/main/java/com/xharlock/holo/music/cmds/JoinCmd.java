package com.xharlock.holo.music.cmds;

import java.util.concurrent.TimeUnit;

import com.xharlock.holo.misc.Emoji;
import com.xharlock.holo.music.core.MusicCommand;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class JoinCmd extends MusicCommand {

	public JoinCmd(String name) {
		super(name);
		setDescription("Use this command to me bring to your current voice channel");
		setUsage(name);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);
		
		EmbedBuilder builder = new EmbedBuilder();
		AudioManager audioManager = e.getGuild().getAudioManager();
		
		if (isBotInAudioChannel(e)) {
			builder.setTitle("Error");
			builder.setDescription("I'm already in a voice channel!\nJoin me in <#" + e.getGuild().getSelfMember().getVoiceState().getChannel().getIdLong() + ">");
			sendEmbed(e, builder, 1, TimeUnit.MINUTES, false);
			return;
		}

		if (!isUserInAudioChannel(e)) {
			builder.setTitle("Not in a voice channel!");
			builder.setDescription("Please join a voice channel first");
			sendEmbed(e, builder, 1, TimeUnit.MINUTES, false);
			return;
		}

		audioManager.openAudioConnection(e.getMember().getVoiceState().getChannel());

		builder.setTitle("Connected " + Emoji.NOTE.getAsNormal());
		builder.setDescription("Join me in <#" + e.getMember().getVoiceState().getChannel().getIdLong() + ">");
		
		sendEmbed(e, builder, 5, TimeUnit.MINUTES, false);
	}
}
