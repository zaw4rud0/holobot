package com.xharlock.holo.commands.owner;

import java.util.Arrays;
import java.util.List;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class NicknameCmd extends Command {

	public NicknameCmd(String name) {
		super(name);
		setDescription("(Owner-only) Use this command to set or change my nickname");
		setUsage(name + " [nickname]");
		setAliases(List.of("nick"));
		setIsGuildOnlyCommand(true);
		setIsOwnerCommand(true);
		setCommandCategory(CommandCategory.OWNER);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		deleteInvoke(e);

		if (args.length >= 1 && args[0].equals("self")) {
			e.getGuild().getMember(e.getJDA().getSelfUser()).modifyNickname(String.join(" ", Arrays.copyOfRange(args, 1, args.length))).queue();
		} else {
			long id = Long.parseLong(args[0]);
			e.getGuild().getMemberById(id).modifyNickname(String.join(" ", Arrays.copyOfRange(args, 1, args.length))).queue();
		}
	}
}