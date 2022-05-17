package com.xharlock.holo.games.pokemon;

import com.xharlock.holo.misc.Emote;

import java.awt.*;

public enum PokemonType {
	
	NORMAL("Normal", Emote.TYPE_NORMAL, new Color(168, 168, 120)),
	FIRE("Fire", Emote.TYPE_FIRE, new Color(240, 128, 48)),
	FIGHTING("Fighting", Emote.TYPE_FIGHTING, new Color(192, 48, 40)),
	FLYING("Flying", Emote.TYPE_FLYING, new Color(168, 144, 240)),
	WATER("Water", Emote.TYPE_WATER, new Color(104, 144, 240)),
	GRASS("Grass", Emote.TYPE_GRASS, new Color(120, 200, 80)),
	ELECTRIC("Electric", Emote.TYPE_ELECTRIC, new Color(248, 228, 48)),
	POISON("Poison", Emote.TYPE_POISON, new Color(160, 64, 160)),
	DARK("Dark", Emote.TYPE_DARK, new Color(112, 88, 72)),
	FAIRY("Fairy", Emote.TYPE_FAIRY, new Color(238, 153, 172)),
	PSYCHIC("Psychic", Emote.TYPE_PSYCHIC, new Color(248, 88, 136)),
	STEEL("Steel", Emote.TYPE_STEEL, new Color(184, 184, 208)),
	ROCK("Rock", Emote.TYPE_ROCK, new Color(184, 160, 56)),
	GROUND("Ground", Emote.TYPE_GROUND, new Color(224, 192, 104)),
	BUG("Bug", Emote.TYPE_BUG, new Color(168, 184, 32)),
	DRAGON("Dragon", Emote.TYPE_DRAGON, new Color(112, 56, 248)),
	GHOST("Ghost", Emote.TYPE_GHOST, new Color(112, 88, 152)),
	ICE("Ice", Emote.TYPE_ICE, new Color(152, 216, 216));

	private final String name;
	private final Emote emote;
	private final Color color;
	
	PokemonType(String name, Emote emote, Color color) {
		this.name = name;
		this.emote = emote;
		this.color = color;
	}
	
	public String getName() {
		return name;
	}
	
	public Emote getEmote() {
		return emote;
	}
	
	public Color getColor() {
		return color;
	}
}