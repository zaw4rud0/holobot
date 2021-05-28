package com.xharlock.otakusenpai.commands.cmds;

import com.xharlock.otakusenpai.commands.core.Command;
import com.xharlock.otakusenpai.commands.core.CommandCategory;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class UwuCmd extends Command {

	public UwuCmd(String name) {
		super(name);
		setDescription("Use this command to translate a sentence into the holy and sacred UwU language.");
		setUsage(name + " <text>");
		setCommandCategory(CommandCategory.MISC);
	}

	// TODO Optimization
	
	@Override
	public void onCommand(MessageReceivedEvent e) {
		if (e.isFromGuild())
			e.getMessage().delete().queue();
		
		e.getChannel().sendTyping().queue();
		
		if (e.getMessage().getReferencedMessage() != null)
			e.getChannel().sendMessage(uwuify(e.getMessage().getReferencedMessage().getContentRaw().split(" "))).queue();
		else
			e.getChannel().sendMessage(uwuify(args)).queue();
	}
	
	private String uwuify(String[] raw) {		
		String result = "";		
		for (String s : raw) {
			String word = s.replace("you", "uwu").replace("You", "Uwu").replace("r", "w").replace("R", "W").replace("l", "w").replace("L", "W").replace("at", "awt").replace("it", "iwt").replace("It", "Iwt").replace("is", "iws").replace("Is", "Iws").replace("to", "tuwu");			
			result += word + " ";
		}
		return result;
	}
}
