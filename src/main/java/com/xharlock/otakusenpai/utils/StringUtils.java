package com.xharlock.otakusenpai.utils;

public class StringUtils {
	
	public static String firstLetterUp(String string) {
        return String.valueOf(string.substring(0, 1).toUpperCase()) + string.substring(1);
    }	
}
