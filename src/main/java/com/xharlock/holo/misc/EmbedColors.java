package com.xharlock.holo.misc;

public enum EmbedColors {

	// TODO More colors
	
	RED(16711680), BLUE(0), GREEN(0), YELLOW(0), ORANGE(0), CYAN(0), LIME(0), PURPLE(0), PINK(0), WHITE(0), BLACK(0),
	GRAY(3092790),

	POKEMON_RED(15500420), POKEMON_BLUE(9755630), POKEMON_BLUE2(10336245), POKEMON_YELLOW(16777113),
	POKEMON_GREEN(6607716), POKEMON_BLACK(12303291), POKEMON_BROWN(13408614), POKEMON_PURPLE(12682177),
	POKEMON_GRAY(13750752), POKEMON_WHITE(14929616), POKEMON_PINK(16039369);

	private int color;

	EmbedColors(int color) {
		this.color = color;
	}

	public int getColor() {
		return this.color;
	}
}
