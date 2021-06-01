package com.xharlock.holo.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

	private static final String path = "./src/main/resources/database/OtakuSenpai.db";
	private static Connection conn;
	
	public static void connect() throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC" + path);
		String url = "jdbc:sqlite:";
		Database.conn = DriverManager.getConnection(url);
	}
	
	public static void disconnect() throws SQLException {
		if (conn != null) {
			conn.close();
		}
	}
	
	public static void execute(String s) throws SQLException {
		Statement st = Database.conn.createStatement();
		st.execute(s);
	}
	
	public static ResultSet query(String s) throws SQLException {
		Statement st = Database.conn.createStatement();
		return st.executeQuery(s);
	}
}
