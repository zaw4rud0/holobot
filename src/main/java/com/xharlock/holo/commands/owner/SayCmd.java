package com.xharlock.holo.commands.owner;

import java.util.Arrays;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SayCmd extends Command {

	public SayCmd(String name) {
		super(name);
		setDescription("(Owner-only) Use this command to repeat a given message as often as you want");
		setUsage(name + " [#repetitions] [message]");
		setIsOwnerCommand(true);
		setIsGuildOnlyCommand(true);
		setCommandCategory(CommandCategory.OWNER);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		e.getMessage().delete().queue();
		String sentence = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
		int times = Integer.parseInt(args[0]);
		for (int i = 0; i < times; i++)
			e.getChannel().sendMessage(sentence.replace("\\n", "\n")).queue();
	}

}
