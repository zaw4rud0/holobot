package com.xharlock.otakusenpai.utils;

import java.util.concurrent.TimeUnit;

public class Formatter {

	public static String formatTrackTime(long timeInMillis) {
        long hours = timeInMillis / TimeUnit.HOURS.toMillis(1L);
        long minutes = timeInMillis % TimeUnit.HOURS.toMillis(1L) / TimeUnit.MINUTES.toMillis(1L);
        long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1L) / TimeUnit.SECONDS.toMillis(1L);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
	
	public static String firstLetterUp(String string) {
        return String.valueOf(string.substring(0, 1).toUpperCase()) + string.substring(1);
    }
	
}
