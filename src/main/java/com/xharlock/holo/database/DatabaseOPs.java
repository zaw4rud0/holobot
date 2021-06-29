package com.xharlock.holo.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.User;

public class DatabaseOPs {
	
	public static boolean addBlockedImage(String url, User user, String date) throws SQLException {
		String s = "Insert into BlockedImages (Url, DiscordUser, Date) VALUES "
				+ "(\'" + url + "\', " + user.getIdLong() + ", \'" + date + "\');";
		Database.connect();
		boolean success = Database.execute(s);
		Database.disconnect();
		return success;
	}
	
	public static List<String> getBlockedImages() throws SQLException {
		String s = "SELECT Url FROM BlockedImages";
		Database.connect();
		ResultSet rs = Database.query(s);
		
		List<String> urls = new ArrayList<>();
		
		while (rs.next()) {
			urls.add(rs.getString("Url"));
		}
		
		Database.disconnect();		
		return urls;
	}
}
