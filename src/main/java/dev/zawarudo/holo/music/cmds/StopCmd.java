package dev.zawarudo.holo.music.cmds;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.music.core.AbstractMusicCommand;
import dev.zawarudo.holo.music.core.GuildMusicManager;
import dev.zawarudo.holo.music.core.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

@Command(name = "stop",
		description = "Stops the current song and clears the queue.",
		ownerOnly = true,
		category = CommandCategory.MUSIC)
public class StopCmd extends AbstractMusicCommand {

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);
		
		EmbedBuilder builder = new EmbedBuilder();
		GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());

		if (musicManager.scheduler.audioPlayer.getPlayingTrack() == null && musicManager.scheduler.queue.isEmpty()) {
			builder.setTitle("Error");
			builder.setDescription("I'm currently idle!");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, true);
			return;
		}

		musicManager.clear();
		
		builder.setTitle("Success");
		builder.setDescription("Stopped current track and cleared queue!");
		sendEmbed(e, builder, 15, TimeUnit.SECONDS, false);
	}
}