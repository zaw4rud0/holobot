package dev.zawarudo.holo.utils;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public final class Formatter {

	private Formatter() {
	}
	
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
		StringBuilder formatted = new StringBuilder();
		if (days > 0) {
			formatted.append(days).append(" days, ");
		}
		if (hours > 0) {
			formatted.append(hours).append(" hours, ");
		}
		if (minutes > 0) {
			formatted.append(minutes).append(" minutes and ");
		}
		return formatted.append(seconds).append(" seconds").toString();
	}

	/**
	 * Turns a given amount of milliseconds to a date and time.
	 */
	public static String formatDateTime(long millis) {
		DateFormat f = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.GERMANY);
		return f.format(new Date(millis));
	}

	/**
	 * Returns the same String but with the first letter in uppercase.
	 */
	public static String capitalize(String string) {
		return string.substring(0, 1).toUpperCase(Locale.UK) + string.substring(1);
	}

	public static String escape(String raw) {
		return raw.replace(" ", "%20")
				.replace("!", "%21")
				.replace("“", "%22")
				.replace("#", "%23")
				.replace("$", "%24")
				.replace("%", "%25")
				.replace("&", "%26")
				.replace("‘", "%27")
				.replace("(", "%28")
				.replace(")", "%29")
				.replace("*", "%2A")
				.replace("+", "%2B")
				.replace(",", "%2C")
				.replace("-", "%2D")
				.replace(".", "%2E")
				.replace("/", "%2F")
				.replace(":", "%3A")
				.replace(";", "%3B")
				.replace("<", "%3C")
				.replace("=", "%3D")
				.replace(">", "%3E")
				.replace("?", "%3F")
				.replace("@", "%40")
				.replace("`", "%60")
				.replace("/", "%2F")
				.replace("~", "%7E")
				.replace("\\", "%5C")
				.replace("|", "%7C")
				.replace("{", "%7B")
				.replace("}", "%7D")
				.replace("[", "%5B")
				.replace("]", "%5D")
				.replace("^", "%5E");
	}
}