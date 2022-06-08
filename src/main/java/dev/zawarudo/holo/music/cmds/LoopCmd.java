package dev.zawarudo.holo.music.cmds;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.annotations.Deactivated;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.music.core.AbstractMusicCommand;
import dev.zawarudo.holo.music.core.GuildMusicManager;
import dev.zawarudo.holo.music.core.PlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Deactivated
@Command(name = "loop",
		description = "Loops the current song",
		category = CommandCategory.MUSIC)
public class LoopCmd extends AbstractMusicCommand {

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);
		
		GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(e.getGuild());
		boolean repeating = !musicManager.scheduler.looping;
		musicManager.scheduler.looping = repeating;
		e.getChannel().sendMessageFormat("Loop %s", repeating ? "enabled" : "disabled").queue();
	}
}