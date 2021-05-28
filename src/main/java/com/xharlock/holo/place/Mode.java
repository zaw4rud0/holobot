package com.xharlock.holo.place;

public enum Mode {

	NONE("none"),
	BULLY_BACKGROUND("background"),
	BULLY_RANDOM("random"),
	BULLY_RESTORE("bully_restore"),
	PROTECT("protect")
	;
	
	private String name;
	
	Mode(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
}
