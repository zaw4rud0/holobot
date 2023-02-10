package dev.zawarudo.holo.owner;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Command(name = "echo",
		description = "Repeats a given message by a given amount.",
		usage = "<amount> <message>",
		alias = {"say", "repeat"},
		ownerOnly = true,
		category = CommandCategory.OWNER)
public class EchoCmd extends AbstractCommand {

	@Override
	public void onCommand(@NotNull MessageReceivedEvent e) {
		deleteInvoke(e);

		// TODO: Check for different cases, such as no amount, no message, etc. was given.

		String sentence = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
		int times = Integer.parseInt(args[0]);
		for (int i = 0; i < times; i++) {
			e.getChannel().sendMessage(sentence.replace("\\n", "\n")).queue();
		}
	}
}