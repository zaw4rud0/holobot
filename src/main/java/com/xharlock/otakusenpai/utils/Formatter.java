package com.xharlock.otakusenpai.utils;

import java.util.concurrent.TimeUnit;

public class Formatter {

	public static String formatTrackTime(long timeInMillis) {
		long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);
		long minutes = timeInMillis % TimeUnit.HOURS.toMillis(1) / TimeUnit.MINUTES.toMillis(1);
		long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}

	public static String formatTime(long timeInMillis) {
		long days = timeInMillis / TimeUnit.DAYS.toMillis(1);
		long hours = timeInMillis % TimeUnit.DAYS.toMillis(1) / TimeUnit.HOURS.toMillis(1);
		long minutes = timeInMillis % TimeUnit.HOURS.toMillis(1) / TimeUnit.MINUTES.toMillis(1);
		long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);		
		String formatted = "`";		
		if (days > 0)
			formatted += days + " days, ";
		if (hours > 0)
			formatted += hours + " hours, ";
		if (minutes > 0)
			formatted += minutes + " minutes, ";		
		return formatted += seconds + " seconds`";
	}

	public static String firstLetterUp(String string) {
		return String.valueOf(string.substring(0, 1).toUpperCase()) + string.substring(1);
	}

	public static String escapeCharacters(String raw) {
		return raw.replace(" ", "%20").replace("“", "%22").replace("#", "%23").replace("$", "%24").replace("&", "%26")
				.replace("‘", "%27").replace("@", "%40").replace("`", "%60").replace("/", "%2F").replace("?", "%3F")
				.replace(",", "%2C").replace(":", "%3A").replace(";", "%3B").replace("+", "%2B").replace("~", "%7E");
	}
}