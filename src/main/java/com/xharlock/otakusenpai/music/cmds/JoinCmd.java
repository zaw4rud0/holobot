package com.xharlock.otakusenpai.music.cmds;

import java.util.concurrent.TimeUnit;

import com.xharlock.otakusenpai.misc.Emojis;
import com.xharlock.otakusenpai.music.core.MusicCommand;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class JoinCmd extends MusicCommand {

	public JoinCmd(String name) {
		super(name);
		setDescription("Use this command to me bring to your current voice channel");
		setUsage(name);
		setIsOwnerCommand(true);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		e.getMessage().delete().queue();
		
		e.getChannel().sendTyping().queue();
		
		EmbedBuilder builder = new EmbedBuilder();
		AudioManager audioManager = e.getGuild().getAudioManager();

		if (isBotInChannel(e)) {
			builder.setTitle("Error");
			builder.setDescription("I'm already in a voice channel!\nJoin me in " + e.getGuild().getSelfMember().getVoiceState().getChannel().getAsMention());
			sendEmbed(e, builder, 1, TimeUnit.MINUTES, false);
			return;
		}

		if (!isUserInChannel(e)) {
			return;
		}

		audioManager.openAudioConnection(e.getMember().getVoiceState().getChannel());

		builder.setTitle("Connected " + Emojis.NOTE.getAsNormal());
		builder.setDescription("Join me in " + e.getMember().getVoiceState().getChannel().getAsMention());		
		sendEmbed(e, builder, 5, TimeUnit.MINUTES, false);
	}
}
