package com.xharlock.holo.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public final class Reader {

	private Reader() {
	}
	
	public static String readLine(String filePath) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(filePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String s = scanner.nextLine();
		scanner.close();
		return s;
	}
}