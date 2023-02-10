package dev.zawarudo.holo.music.cmds;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.music.AbstractMusicCommand;
import dev.zawarudo.holo.music.GuildMusicManager;
import dev.zawarudo.holo.music.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

@Command(name = "leave",
		description = "Makes me leave the current voice channel.",
		ownerOnly = true,
		category = CommandCategory.MUSIC)
public class LeaveCmd extends AbstractMusicCommand {

	@Override
	public void onCommand(@NotNull MessageReceivedEvent event) {
		deleteInvoke(event);

		Guild guild = event.getGuild();
		EmbedBuilder builder = new EmbedBuilder();

		if (!isBotInAudioChannel(guild)) {
			builder.setTitle("Error");
			builder.setDescription("I'm not in any voice channel at the moment!");
			sendEmbed(event, builder, false, 15, TimeUnit.SECONDS);
			return;
		}

		GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(guild);
		musicManager.clear();

		guild.getAudioManager().closeAudioConnection();

		builder.setTitle("Disconnected");
		builder.setDescription("See you soon!");
		sendEmbed(event, builder, false, 30, TimeUnit.SECONDS);
	}
}