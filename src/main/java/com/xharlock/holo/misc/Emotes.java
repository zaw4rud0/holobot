package com.xharlock.holo.misc;

public enum Emotes {
	PEPELOVE("pepelove:806641947292860466"),
	HOLOSMUG(""),
	
	HOLOSMASH1("HoloSmash1:841655527796244500"),
	HOLOSMASH2("HoloSmash2:841655571064553523"),
	
	TICK("tick:824297786102644757"),
	CROSS("cross:824297739377573910"),
	UNDO("undo:824289332688060416"),
	CONTINUE("continue:824749379842998283"),
	
	// Pokémon Balls
	POKE_BALL("Poké_Ball:847677146116915260"),
	GREAT_BALL("Great_Ball:847677175931076679"),
	ULTRA_BALL("Ultra_Ball:847677210898071562"),
	MASTER_BALL("Master_Ball:847677098034069514"),
	
	// Pokémon Types
	TYPE_NORMAL("type_normal:805109990393905192"), 
    TYPE_FIRE("type_fire:805109990473596948"), 
    TYPE_FIGHTING("type_fighting:805109990112886785"), 
    TYPE_FLYING("type_flying:805109990305562634"), 
    TYPE_WATER("type_water:805109990536642620"), 
    TYPE_GRASS("type_grass:805109990259687496"), 
    TYPE_ELECTRIC("type_electric:805109990138314804"), 
    TYPE_POISON("type_poison:805109990448168960"), 
    TYPE_DARK("type_dark:805109990301368400"), 
    TYPE_FAIRY("type_fairy:805109990372278292"), 
    TYPE_PSYCHIC("type_psychic:805109990163218523"), 
    TYPE_STEEL("type_steel:805109990444105748"), 
    TYPE_ROCK("type_rock:805109990447775744"), 
    TYPE_GROUND("type_ground:805109990284066817"), 
    TYPE_BUG("type_bug:805109990158630953"), 
    TYPE_DRAGON("type_dragon:805109989902909481"), 
    TYPE_GHOST("type_ghost:805109990754222111"),
    TYPE_ICE("type_ice:805109990394298398");
    
    private String id;
    
    Emotes(String id) {
        this.id = id;
    }
    
    public String getAsReaction() {
        return id;
    }
    
    public String getAsText() {
        return "<:" + id + ">";
    }
}
