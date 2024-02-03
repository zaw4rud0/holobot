package dev.zawarudo.holo.modules.jikan.model;

import com.google.gson.annotations.SerializedName;

public final class Broadcast {
	@SerializedName("day")
	private String day;
	@SerializedName("time")
	private String time;
	@SerializedName("timezone")
	private String timeZone;
	@SerializedName("string")
	private String string;

	/**
	 * Returns the day when episodes of this anime are or were being released.
	 */
	public String getDay() {
		return day;
	}

	/**
	 * Returns the time when episodes of this anime are or were being released. Make
	 * sure to check the time zone using the {@link #getTimeZone()} method.
	 */
	public String getTime() {
		return time;
	}

	/**
	 * Returns the time zone.
	 */
	public String getTimeZone() {
		return timeZone;
	}

	/**
	 * Returns the day and time as a formatted String.
	 */
	public String getString() {
		return string;
	}
}