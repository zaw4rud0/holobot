package com.xharlock.holo.commands.owner;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ShutdownCmd extends Command {

	public ShutdownCmd(String name) {
		super(name);
		setDescription("(Owner-only) Use this command to shutdown the bot");
		setUsage(name);
		setIsOwnerCommand(true);
		setCommandCategory(CommandCategory.OWNER);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		// Leaves all voice channels so bot doesn't get stuck after restarting
		e.getJDA().getGuilds().stream().filter(g -> g.getSelfMember().getVoiceState().inAudioChannel()).forEach(g -> g.getAudioManager().closeAudioConnection());
		Runtime.getRuntime().exit(0);
	}
}
