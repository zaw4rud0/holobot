package com.xharlock.otakusenpai.core;

import net.dv8tion.jda.api.events.ExceptionEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReadyListener extends ListenerAdapter {

	@Override
	public void onReady(ReadyEvent e) {
		System.out.println(String.format("%s is ready!\n", e.getJDA().getSelfUser().getAsTag()));
		Main.otakuSenpai.registerManagers();
		Main.otakuSenpai.registerListeners();
	}
	
	public void onException(ExceptionEvent e) {
		e.getCause().printStackTrace();
	}
	
}
