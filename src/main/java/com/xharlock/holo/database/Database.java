package com.xharlock.holo.database;

import java.sql.*;

/**
 * Core class for database connection and operations.
 */
public final class Database {

	/** The path to the database file. */
	//public static final String PATH_DB = "./src/main/resources/database/Holo.db";
	public static final String PATH_DB = "./src/main/resources/database/HoloTest.db";

	private Database() {
	}

	/**
	 * Creates a connection to the database.
	 */
	public static Connection getConnection() throws SQLException {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// This error should never occur.
			throw new RuntimeException("Could not find SQLite JDBC driver.");
		}
		String url = "jdbc:sqlite:" + PATH_DB;
		return DriverManager.getConnection(url);
	}
}