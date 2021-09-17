package com.xharlock.holo.misc;

public enum Emojis {

	// https://www.emojiall.com/en

	THUMBSUP("U+1F44D", ":thumbsup:", "ðŸ‘?"),
	THUMBSDOWN("U+1F44E", ":thumbsdown:", "ðŸ‘Ž"),
	HEART("U+2764", ":heart:", "â?¤ï¸?"),
	BLUSH("U+1F633", ":blush:", "ðŸ˜Š"), 
	FLUSHED("", ":flushed:", "ðŸ˜³"),
	ZZZ("U+1F4A4", ":zzz:", "ðŸ’¤"),

	// Arrows and other direction signs
	UPVOTE("U+2B06", ":arrow_up:", "â¬†ï¸?"), 
	DOWNVOTE("U+2B07", ":arrow_down:", "â¬‡ï¸?"),
	ARROW_LEFT("U+2B05", ":arrow_left:", "â¬…ï¸?"), 
	ARROW_RIGHT("U+27A1", ":arrow_right:", "âž¡ï¸?"),

	// Music and sound emojis
	MUTED("U+1F507", ":mute:", "ðŸ”‡"), 
	SPEAKER("U+1F508", ":speaker:", "ðŸ”ˆ"), 
	SPEAKER_QUIET("U+1F509", ":sound:", "ðŸ”‰"),
	SPEAKER_LOUD("U+1F50A", ":loud_sound:", "ðŸ”Š"), 
	BELL("U+1F514", ":bell:", "ðŸ””"),
	NO_BELL("U+1F515", "no_bell:", "ðŸ”•"), 
	LOUDSPEAKER("U+1F4E2", ":loudspeaker:", "ðŸ“¢"),
	MEGAPHONE("U+1F4E3", ":mega:", "ðŸ“£"), 
	POSTAL_HORN("U+1F4EF", ":postal_horn:", "ðŸ“¯"),
	NOTE("U+1F3B5", ":musical_note:", "ðŸŽµ"), 
	NOTES("U+1F3B6", ":notes:", "ðŸŽ¶"),
	MICROPHONE("U+1F3A4", ":microphone:", "ðŸŽ¤"), 
	MICROPHONE2("U+1F399", ":microphone2:", "ðŸŽ™ï¸?"),
	HEADPHONES("U+1F3A7", ":headphones:", "ðŸŽ§"), 
	RADIO("U+1F4FB", ":radio:", "ðŸ“»"),

	// Numbers
	ZERO("U+0030", ":zero:", "0ï¸?âƒ£"), 
	ONE("U+0031", ":one:", "1ï¸?âƒ£"), 
	TWO("U+0032", ":two:", "2ï¸?âƒ£"),
	THREE("U+0033", ":three:", "3ï¸?âƒ£"), 
	FOUR("U+0034", ":four:", "4ï¸?âƒ£"), 
	FIVE("U+0035", ":five:", "5ï¸?âƒ£"),
	SIX("U+0036", ":six:", "6ï¸?âƒ£"), 
	SEVEN("U+0037", ":seven:", "7ï¸?âƒ£"), 
	EIGHT("U+0038", ":eight:", "8ï¸?âƒ£"),
	NINE("U+0039", ":nine:", "9ï¸?âƒ£"), 
	TEN("U+1F51F", ":keycap_ten:", "ðŸ”Ÿ"), 
	HUNDRED("U+1F4AF", ":100:", "ðŸ’¯"),

	// Signs and warnings
	WARNING("U+26A0", ":warning", "âš ï¸?"), 
	NO_ENTRY("U+26D4", ":no_entry:", "â›”"),
	PROHIBITED("U+1F6AB", ":no_entry_sign:", "ðŸš«"), 
	RADIOACTIVE("U+2622", ":radioactive:", "â˜¢ï¸?"),
	BIOHAZARD("U+2623", ":biohazard:", "â˜£ï¸?"), 
	UNDERAGE("U+1F51E", ":underage:", "ðŸ”ž"),;

	private String unicode;
	private String normal_version;
	private String browser_version;

	Emojis(String unicode, String normal, String browser) {
		this.unicode = unicode;
		this.normal_version = normal;
		this.browser_version = browser;
	}

	/**
	 * The emoji as reaction
	 */
	public String getAsUnicode() {
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
