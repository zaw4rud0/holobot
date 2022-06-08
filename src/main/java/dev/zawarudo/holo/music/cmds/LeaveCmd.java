package dev.zawarudo.holo.music.cmds;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.music.core.AbstractMusicCommand;
import dev.zawarudo.holo.music.core.GuildMusicManager;
import dev.zawarudo.holo.music.core.PlayerManager;
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