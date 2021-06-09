package com.xharlock.holo.commands.cmds; 

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ButtonEventListener extends ListenerAdapter {

	@Override
	public void onButtonClick(ButtonClickEvent e) {		
		if (e.getButton().getUrl() != null && e.getButton().getUrl().equals("https://www.youtube.com/watch?v=dQw4w9WgXcQ")) {
			e.getChannel().sendMessage(e.getMember().getAsMention() + " has been rickrolled!").queue();
		}
		
		else {
			e.getChannel().sendMessage(e.getButton().getLabel() + " has been clicked by " + e.getMember().getAsMention() + "!").queue();
		}
	}
	
}
