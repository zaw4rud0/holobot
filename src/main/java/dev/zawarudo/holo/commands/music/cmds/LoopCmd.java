package dev.zawarudo.holo.commands.music.cmds;

import dev.zawarudo.holo.utils.annotations.Command;
import dev.zawarudo.holo.utils.annotations.Deactivated;
import dev.zawarudo.holo.commands.music.AbstractMusicCommand;
import dev.zawarudo.holo.commands.music.GuildMusicManager;
import dev.zawarudo.holo.commands.music.PlayerManager;
import dev.zawarudo.holo.commands.CommandCategory;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

// TODO: Fully implement the command

@Deactivated
@Command(name = "loop",
		description = "Loops the current song",
		category = CommandCategory.MUSIC)
public class LoopCmd extends AbstractMusicCommand {

	@Override
	public void onCommand(@NotNull MessageReceivedEvent e) {
		deleteInvoke(e);
		
		GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());
		boolean repeating = !musicManager.scheduler.looping;
		musicManager.scheduler.looping = repeating;
		e.getChannel().sendMessageFormat("Loop %s", repeating ? "enabled" : "disabled").queue();
	}
}