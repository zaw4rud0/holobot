package com.xharlock.otakusenpai.misc;

public enum Emojis {

	// https://www.emojiall.com/en
	
	// TODO Each emoji can have multiple different appearances such as 👍
	
	THUMBSUP("U+1F44D", ":thumbsup:", "👍"),
	THUMBSDOWN("U+1F44E", ":thumbsdown:", "👎"),
	HEART("U+2764", ":heart:", "❤️"),
	BLUSH("U+1F633", ":blush:", "😊"),
	FLUSHED("", ":flushed:", "😳"),
	ZZZ("U+1F4A4", ":zzz:", "💤"),
	
	// Arrows and other direction signs
	UPVOTE("U+2B06", ":arrow_up:", "⬆️"),
	DOWNVOTE("U+2B07", ":arrow_down:", "⬇️"),
	ARROW_LEFT("U+2B05", ":arrow_left:", "⬅️"),
	ARROW_RIGHT("U+27A1", ":arrow_right:", "➡️"),
	
	// Music and sound emojis
	SPEAKER_MUTED("U+1F507", ":mute:", ""),
	SPEAKER("U+1F508", ":speaker:", ""),
	SPEAKER_SOUND("U+1F509", ":sound:", ""),
	SPEAKER_LOUD("U+1F50A", ":loud_sound:", ""),
	BELL("U+1F514", ":bell:", ""),
	NO_BELL("U+1F515", "no_bell:", ""),
	LOUDSPEAKER("U+1F4E2", ":loudspeaker:", ""),
	MEGAPHONE("U+1F4E3", ":mega:", ""),
	POSTAL_HORN("U+1F4EF", ":postal_horn:", ""),
	NOTE("U+1F3B5", ":musical_note:", ""),
	NOTES("U+1F3B6", ":notes:", ""),
	MICROPHONE("U+1F3A4", ":microphone:", ""),
	MICROPHONE2("U+1F399", ":microphone2:", ""),
	HEADPHONES("U+1F3A7", ":headphones:", ""),
	RADIO("U+1F4FB", ":radio:", ""),
	
	// Numbers
	ZERO("U+0030", ":zero:", "0️⃣"),
	ONE("U+0031", ":one:", "1️⃣"),
	TWO("U+0032", ":two:", "2️⃣"),
	THREE("U+0033", ":three:", "3️⃣"),
	FOUR("U+0034", ":four:", "4️⃣"),
	FIVE("U+0035", ":five:", "5️⃣"),
	SIX("U+0036", ":six:", "6️⃣"),
	SEVEN("U+0037", ":seven:", "7️⃣"),
	EIGHT("U+0038", ":eight:", "8️⃣"),
	NINE("U+0039", ":nine:", "9️⃣"),
	TEN("U+1F51F", ":keycap_ten:", "🔟"),
	HUNDRED("U+1F4AF", ":100:", "💯"),
	
	// Signs and warnings
	WARNING("U+26A0", ":warning", "⚠️"),
	NO_ENTRY("U+26D4", ":no_entry:", "⛔"),
	PROHIBITED("U+1F6AB", ":no_entry_sign:", "🚫"),
	RADIOACTIVE("U+2622", ":radioactive:", "☢️"),
	BIOHAZARD("U+2623", ":biohazard:", "☣️"),
	UNDERAGE("U+1F51E", ":underage:", "🔞"),
	;
	
	private String unicode;
	private String normal_version;
	private String browser_version;
	
	Emojis(String unicode, String normal, String browser){
		this.unicode = unicode;
		this.normal_version = normal;
		this.browser_version = browser;
	}
	
	/**
	 * The emoji as reaction
	 */
	public String getAsReaction() {
		return this.unicode;
	}
	
	/**
	 * The discord version of this emoji
	 */
	public String getAsNormal() {
		return this.normal_version;
	}
	
	/**
	 * The browser version of this emoji
	 */
	public String getAsBrowser() {
		return this.browser_version;
	}
}
