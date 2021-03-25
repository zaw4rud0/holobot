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
	
	ZERO("U+0030 U+20E3", ":zero:"),
	ONE("U+0031 U+20E3", ":one:"),
	TWO("U+0032 U+20E3", ":two:"),
	THREE("U+0033 U+20E3", ":three:"),
	FOUR("U+0034 U+20E3", ":four:"),
	FIVE("U+0035 U+20E3", ":five:"),
	SIX("U+0036 U+20E3", ":six:"),
	SEVEN("U+0037 U+20E3", ":seven:"),
	EIGHT("U+0038 U+20E3", ":eight:"),
	NINE("U+0039 U+20E3", ":nine:"),
	TEN("U+1F51F", ":keycap_ten:"),
	HUNDRED("U+1F4AF", ":100:");
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
