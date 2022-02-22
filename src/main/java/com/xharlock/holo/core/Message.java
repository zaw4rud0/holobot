package com.xharlock.holo.core;

/**
 * Enum to keep all the message components. Should help keep all the embeds consistent.
 */
public enum Message {

	TITLE_ERROR("Error");

	private String msg;

	Message(String msg) {
		this.msg = msg;
	}

	public String getMessage() {
		return msg;
	}
}