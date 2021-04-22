package com.xharlock.otakusenpai.commands.core;

public enum CommandCategory {

	GENERAL("General Commands"),
	ANIME("Anime Commands"),
	MUSIC("Music Commands"),
	GAMES("Game Commands"),
	IMAGE("Image Commands"),
	PLACE("Place Commands"),
	MISC("Miscellaneous Commands"),
	ADMIN("Admin Commands"),
	OWNER("Owner Commands"),
	BLANK("Uncategorized Commands");
	
	private String name;
	
	CommandCategory(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
}
