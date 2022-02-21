package com.xharlock.holo.core;

public enum Message {

	ERROR("Error");

	private String msg;

	Message(String msg) {
		this.msg = msg;
	}

	public String getMessage() {
		return msg;
	}
}
