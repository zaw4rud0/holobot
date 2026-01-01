package dev.zawarudo.holo.commands.owner;

import dev.zawarudo.holo.utils.annotations.CommandInfo;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@CommandInfo(name = "echo",
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