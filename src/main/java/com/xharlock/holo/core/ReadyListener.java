package com.xharlock.holo.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Deprecated
public class ReadyListener extends ListenerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(ReadyListener.class);
	
	@Override
	public void onReady(ReadyEvent e) {
		logger.info(String.format("%s is ready!\n", e.getJDA().getSelfUser().getAsTag()));
		Bootstrap.holo.registerManagers();
		Bootstrap.holo.registerListeners();
	}
}
