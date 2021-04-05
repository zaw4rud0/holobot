package com.xharlock.otakusenpai.db;

import java.sql.SQLException;

public class DatabaseTest {
	
	public static void main(String[] args) {
		
		String s = "INSERT INTO Config (Guild_Id, Owner_Id, Prefix) VALUES (778991087847079969, 802472545172455444, '<');";
        try {
			Database.connect();
			Database.execute(s);
	        Database.disconnect();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
}
