package com.xharlock.otakusenpai.misc;

public enum Emojis {

	THUMBSUP("U+1F44D", ":thumbsup:"),
	THUMBSDOWN("U+1F44E", ":thumbsdown:"),
	HEART("U+2764", ":heart:"),
	BLUSH("U+1F633", ":blush:"),
	
	UPVOTE("U+2B06", ":arrow_up:"),
	DOWNVOTE("U+2B07", ":arrow_down:"),
	
	SPEAKER("U+1F50A", ":loud_sound:"),
	NOTE("U+1F3B5", ":musical_note:"),
	NOTES("U+1F3B6", ":notes:"),
	
	ZERO("0️⃣", ":zero:"),
	ONE("1⃣", ":one:"),
	TWO("2⃣", ":two:"),
	THREE("3⃣", ":three:"),
	FOUR("4⃣", ":four:"),
	FIVE("5⃣", ":five:"),
	SIX("6⃣", ":six:"),
	SEVEN("7⃣", ":seven:"),
	EIGHT("8⃣", ":eight:"),
	NINE("9⃣", ":nine:"),
	TEN("🔟", ":keycap_ten:"),
	HUNDRED("💯", ":100:");
	;
	
	private String unicode;
	private String text;
	
	Emojis(String unicode, String text){
		this.unicode = unicode;
		this.text = text;
	}
	
	public String getAsReaction() {
		return this.unicode;
	}
	
	public String getAsText() {
		return this.text;
	}
}
