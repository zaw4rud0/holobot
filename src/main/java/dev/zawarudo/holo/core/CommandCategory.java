package dev.zawarudo.holo.core;

/**
 * Category of a {@link AbstractCommand}
 */
public enum CommandCategory {
	GENERAL("General Commands"),
	ANIME("Anime Commands"),
	MUSIC("Music Commands"),
	GAMES("Game Commands"),
	IMAGE("Image Commands"),
	ACTION("Action Commands"),
	MISC("Miscellaneous Commands"),
	EXPERIMENTAL("Experimental Commands"),
	ADMIN("Admin Commands"),
	OWNER("Owner Commands"),
	BLANK("Uncategorized Commands");
	
	private final String name;
	
	CommandCategory(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
}