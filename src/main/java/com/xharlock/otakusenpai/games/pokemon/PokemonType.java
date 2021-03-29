package com.xharlock.otakusenpai.games.pokemon;

import java.awt.Color;

import com.xharlock.otakusenpai.misc.Emotes;

public enum PokemonType {
	
	NORMAL("Normal", Emotes.TYPE_NORMAL, new Color(168, 168, 120)),
	FIRE("Fire", Emotes.TYPE_FIRE, new Color(240, 128, 48)),
	FIGHTING("Fighting", Emotes.TYPE_FIGHTING, new Color(192, 48, 40)),
	FLYING("Flying", Emotes.TYPE_FLYING, new Color(168, 144, 240)),
	WATER("Water", Emotes.TYPE_WATER, new Color(104, 144, 240)),
	GRASS("Grass", Emotes.TYPE_GRASS, new Color(120, 200, 80)),
	ELECTRIC("Electric", Emotes.TYPE_ELECTRIC, new Color(248, 208, 48)),
	POISON("Poison", Emotes.TYPE_POISON, new Color(160, 64, 160)),
	DARK("Dark", Emotes.TYPE_DARK, new Color(112, 88, 72)),
	FAIRY("Fairy", Emotes.TYPE_FAIRY, new Color(238, 153, 172)),
	PSYCHIC("Psychic", Emotes.TYPE_PSYCHIC, new Color(248, 88, 136)),
	STEEL("Steel", Emotes.TYPE_STEEL, new Color(184, 184, 208)),
	ROCK("Rock", Emotes.TYPE_ROCK, new Color(184, 160, 56)),
	GROUND("Ground", Emotes.TYPE_GROUND, new Color(224, 192, 104)),
	BUG("Bug", Emotes.TYPE_BUG, new Color(168, 184, 32)),
	DRAGON("Dragon", Emotes.TYPE_DRAGON, new Color(112, 56, 248)),
	GHOST("Ghost", Emotes.TYPE_GHOST, new Color(112, 88, 152)),
	ICE("Ice", Emotes.TYPE_ICE, new Color(152, 216, 216))	
	;

	private String name;
	private Emotes emote;
	private Color color;
	
	PokemonType(String name, Emotes emote, Color color) {
		this.name = name;
		this.emote = emote;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Emotes getEmote() {
		return this.emote;
	}
	
	public Color getColor() {
		return this.color;
	}
}
