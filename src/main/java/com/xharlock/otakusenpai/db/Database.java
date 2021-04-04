package com.xharlock.otakusenpai.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

	private static Connection conn;

	public static void connect() throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		String url = "jdbc:sqlite:./src/main/resources/db/OtakuSenpai.db";
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
