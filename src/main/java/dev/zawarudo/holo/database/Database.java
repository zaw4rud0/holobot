package dev.zawarudo.holo.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class required for database connection. To use operations on the database, see
 * {@link DBOperations}.
 */
public final class Database {

	/** The path to the database file. */
	public static final String PATH_DB = "./Holo.db";

	private Database() {
		throw new UnsupportedOperationException();
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

	private static DatabaseMetaData getDBMetaData() throws SQLException {
		return getConnection().getMetaData();
	}

	public static boolean tableExists(String tableName) throws SQLException {
		try (ResultSet rs = getDBMetaData().getTables(null, null, tableName, null)) {
			return rs.next();
		}
	}

	public static List<String> getColumnNames(String tableName) throws SQLException {
		List<String> columns = new ArrayList<>();
		try (ResultSet rs = getDBMetaData().getColumns(null, null, tableName, null)) {
			while (rs.next()) {
				columns.add(rs.getString("COLUMN_NAME"));
			}
		}
		return columns;
	}
}