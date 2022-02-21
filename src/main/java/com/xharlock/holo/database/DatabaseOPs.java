package com.xharlock.holo.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.User;

public final class DatabaseOPs {
	
	private DatabaseOPs() {
	}
	
	/**
	 * Method to add a new image to the blocklist into the DB
	 */
	public static boolean addBlockedImage(String url, User user, String date) throws SQLException {
		String s = "Insert into BlockedImages (Url, DiscordUser, Date) VALUES "
				+ "(\'" + url + "\', " + user.getIdLong() + ", \'" + date + "\');";
		Database.connect();
		boolean success = Database.execute(s);
		Database.disconnect();
		return success;
	}
	
	/**
	 * Method to add a requested block into the DB
	 */
	public static boolean addBlockRequest(String url, User user, String date) throws SQLException {
		String s = "Insert into BlockRequests (Url, DiscordUser, Date) VALUES "
				+ "(\'" + url + "\', " + user.getIdLong() + ", \'" + date + "\');";
		Database.connect();
		boolean success = Database.execute(s);
		Database.disconnect();
		return success;
	}
	
	/**
	 * Method to get a list of the blocked images from the DB
	 */
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
	
	/**
	 * Method to get a list of requested blocks from the DB
	 */
	public static List<String> getBlockRequests() throws SQLException {
		String s = "SELECT Url FROM BlockRequests";
		Database.connect();
		ResultSet rs = Database.query(s);
		List<String> urls = new ArrayList<>();
		while (rs.next()) {
			urls.add(rs.getString("Url"));
		}
		Database.disconnect();
		return urls;
	}
	
	/**
	 * Method to add a new waifu to the image command
	 * 
	 * @param name = Name of waifu
	 * @param tag = Gelbooru tag of waifu
	 * @param title = Title of the embed
	 */
	public static boolean insertWaifu(String name, String tag, String title) throws SQLException {
		title = title.replace("'", "''");
		Database.connect();
		String s = "INSERT into Gelbooru (Id, Tag, Title) VALUES (\'" + name + "\', \'" + tag + "\', \'" + title + "\');";
		boolean success = Database.execute(s);
		Database.disconnect();
		return success;
	}
	
	/**
	 * Method to get all the waifu names from DB
	 */
	public static List<String> getWaifuNames() throws SQLException {
		String s = "SELECT Id FROM Gelbooru";
		Database.connect();
		ResultSet rs = Database.query(s);
		List<String> names = new ArrayList<>();
		while (rs.next()) {
			names.add(rs.getString("Id"));
		}
		Database.disconnect();
		return names;
	}
	
	/**
	 * Method to get the Gelbooru tag and the embed title using the name of the waifu
	 */
	public static ResultSet getWaifu(String name) throws SQLException {
		String s = "SELECT * FROM Gelbooru WHERE Id = \'" + name + "\';";
		Database.connect();
		ResultSet rs = Database.query(s);
		return rs;
	}
}
