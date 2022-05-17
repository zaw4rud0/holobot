package com.xharlock.holo.owner;

import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.core.AbstractCommand;
import com.xharlock.holo.core.CommandCategory;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;

@Command(name = "echo",
		description = "Repeats a given message by a given amount.",
		usage = "<amount> <message>",
		alias = {"say", "repeat"},
		ownerOnly = true,
		category = CommandCategory.OWNER)
public class EchoCmd extends AbstractCommand {

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);

		// TODO: Check for different cases, such as no amount, no message, etc. was given.

		String sentence = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
		int times = Integer.parseInt(args[0]);
		for (int i = 0; i < times; i++) {
			e.getChannel().sendMessage(sentence.replace("\\n", "\n")).queue();
		}
	}
}