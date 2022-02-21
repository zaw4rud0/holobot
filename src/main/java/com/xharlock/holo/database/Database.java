package com.xharlock.holo.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {

	private static final String path = "./src/main/resources/database/Holo.db";
	private static Connection conn;

	private Database() {
	}
	
	public static void connect() throws SQLException {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		String url = "jdbc:sqlite:" + path;
		Database.conn = DriverManager.getConnection(url);
	}
	
	public static void disconnect() throws SQLException {
		if (conn != null) {
			conn.close();
		}
	}
	
	/**
	 * Executes a given SQL statement
	 */
	public static boolean execute(String s) throws SQLException {
		Statement st = Database.conn.createStatement();
		boolean success = st.execute(s);
		st.close();
		return success;
	}
	
	/**
	 * Querys the DB using a statement
	 */
	public static ResultSet query(String s) throws SQLException {
		Statement st = Database.conn.createStatement();
		ResultSet rs = st.executeQuery(s);
		st.close();
		return rs;
	}
}
