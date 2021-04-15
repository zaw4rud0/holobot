package com.xharlock.otakusenpai.commands.owner;

import java.util.List;

import com.xharlock.otakusenpai.commands.core.Command;
import com.xharlock.otakusenpai.commands.core.CommandCategory;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class NicknameCmd extends Command {

	public NicknameCmd(String name) {
		super(name);
		setDescription("(Owner-only) Use this command to set or change my nickname");
		setUsage(name + " [nickname]");
		setAliases(List.of("nick"));
		setIsOwnerCommand(true);
        setCommandCategory(CommandCategory.OWNER);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		e.getMessage().delete().queue();
		e.getGuild().getMember(e.getJDA().getSelfUser()).modifyNickname(String.join(" ", args)).queue();
	}

}
