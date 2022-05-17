package com.xharlock.holo.music.cmds;

import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.core.CommandCategory;
import com.xharlock.holo.music.core.AbstractMusicCommand;
import com.xharlock.holo.music.core.GuildMusicManager;
import com.xharlock.holo.music.core.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

@Command(name = "leave",
		description = "Makes me leave the current voice channel.",
		ownerOnly = true,
		category = CommandCategory.MUSIC)
public class LeaveCmd extends AbstractMusicCommand {

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