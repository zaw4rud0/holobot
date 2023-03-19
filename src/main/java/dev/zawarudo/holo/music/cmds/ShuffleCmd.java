package dev.zawarudo.holo.music.cmds;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.music.AbstractMusicCommand;
import dev.zawarudo.holo.music.GuildMusicManager;
import dev.zawarudo.holo.music.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

@Command(name = "shuffle",
		description = "Shuffles the current queue.",
		category = CommandCategory.MUSIC)
public class ShuffleCmd extends AbstractMusicCommand {

	@Override
	public void onCommand(@NotNull MessageReceivedEvent e) {
		deleteInvoke(e);

		GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());

		if (musicManager.scheduler.queue.isEmpty()) {
			sendErrorEmbed(e, "I can't shuffle an empty queue!");
			return;
		}

		musicManager.scheduler.shuffle();

		String userName = e.getMember() != null ? e.getMember().getEffectiveName() : e.getAuthor().getName();

		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Shuffled Queue");
		builder.setDescription(userName + " shuffled the queue!");
		sendEmbed(e, builder, false, 1, TimeUnit.MINUTES);
	}
}