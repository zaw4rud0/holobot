package com.xharlock.holo.music.cmds;

import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.annotations.Deactivated;
import com.xharlock.holo.core.CommandCategory;
import com.xharlock.holo.music.core.AbstractMusicCommand;
import com.xharlock.holo.music.core.GuildMusicManager;
import com.xharlock.holo.music.core.PlayerManager;
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