package com.xharlock.holo.music.cmds;

import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.annotations.Deactivated;
import com.xharlock.holo.core.CommandCategory;
import com.xharlock.holo.music.core.AbstractMusicCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

// TODO Command that lets an user remove tracks at a specific position. Helpful to remove rickrolls or other unwanted songs.
@Deactivated
@Command(name = "remove",
		description = "Removes a track at a specific position. The index is 0-based.",
		usage = "<index>",
		category = CommandCategory.MUSIC)
public class RemoveCmd extends AbstractMusicCommand {

	@Override
	public void onCommand(MessageReceivedEvent e) {
		e.getChannel().sendMessage("This feature is not implemented yet!").queue();
	}
}