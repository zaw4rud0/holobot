package com.xharlock.holo.music.cmds;

import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.core.CommandCategory;
import com.xharlock.holo.music.core.AbstractMusicCommand;
import com.xharlock.holo.music.core.GuildMusicManager;
import com.xharlock.holo.music.core.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

@Command(name = "shuffle",
		description = "Shuffles the current queue.",
		category = CommandCategory.MUSIC)
public class ShuffleCmd extends AbstractMusicCommand {

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);

		GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());
		EmbedBuilder builder = new EmbedBuilder();

		if (musicManager.scheduler.queue.isEmpty()) {
			builder.setTitle("Error");
			builder.setDescription("I can't shuffle an empty queue!");
			sendEmbed(e, builder, 15L, TimeUnit.SECONDS, true);
			return;
		}

		musicManager.scheduler.shuffle();
		builder.setTitle("Shuffled Queue");
		builder.setDescription(e.getMember().getEffectiveName() + " shuffled the queue!");
		sendEmbed(e, builder, 1L, TimeUnit.MINUTES, false);
	}
}