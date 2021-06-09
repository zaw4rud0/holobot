package com.xharlock.holo.commands.cmds;

import com.xharlock.holo.commands.core.Command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.Button;

public class ButtonCmd extends Command {

	public ButtonCmd(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {		
		Button bt1 = Button.primary("1", "Primary");
		Button bt2 = Button.secondary("2", "Secondary");
		Button bt3 = Button.success("3", "Success");
		Button bt4 = Button.danger("4", "Danger");
		Button bt5 = Button.link("https://www.youtube.com/watch?v=dQw4w9WgXcQ", "Url");
		e.getChannel().sendMessage("Test").setActionRow(bt1, bt2, bt3, bt4, bt5).queue();	
	}

}
