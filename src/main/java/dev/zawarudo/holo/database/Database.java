package dev.zawarudo.holo.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class required for database connection. To use operations on the database, see
 * {@link DBOperations}.
 */
public final class Database {

	/** The path to the database file. */
	public static final String PATH_DB = "./src/main/resources/database/Holo.db";
	//public static final String PATH_DB = "./src/main/resources/database/HoloTest.db";

	private Database() {
	}

	/**
	 * Creates a connection to the database.
	 *
	 * @return The {@link Connection} object to the database.
	 * @throws SQLException If an error occurred while connecting.
	 */
	public static Connection getConnection() throws SQLException {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// This error should never occur.
			throw new NoClassDefFoundError("Could not find SQLite JDBC driver. " + e.getMessage());
		}
		String url = "jdbc:sqlite:" + PATH_DB;
		return DriverManager.getConnection(url);
	}
}