package com.xharlock.holo.music.cmds;

import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.core.CommandCategory;
import com.xharlock.holo.misc.Emoji;
import com.xharlock.holo.music.core.AbstractMusicCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.concurrent.TimeUnit;

@Command(name = "join",
		description = "Makes me join the voice channel you are currently in.",
		category = CommandCategory.MUSIC)
public class JoinCmd extends AbstractMusicCommand {

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

		if (!isUserInAudioChannel(e.getMember())) {
			builder.setTitle("Not in a voice channel!");
			builder.setDescription("Please join a voice channel first");
			sendEmbed(e, builder, 1, TimeUnit.MINUTES, false);
			return;
		}

		audioManager.openAudioConnection(e.getMember().getVoiceState().getChannel());

		builder.setTitle("Connected " + Emoji.NOTE.getAsText());
		builder.setDescription("Join me in <#" + e.getMember().getVoiceState().getChannel().getIdLong() + ">");
		
		sendEmbed(e, builder, 5, TimeUnit.MINUTES, false);
	}
}
