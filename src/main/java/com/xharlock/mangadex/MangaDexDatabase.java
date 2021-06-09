package com.xharlock.mangadex;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class MangaDexDatabase {
	
	private static Connection conn;

	static void connect() throws SQLException {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		String url = "jdbc:sqlite:./src/main/java/com/xharlock/mangadex/MangaDexDB.db";
		conn = DriverManager.getConnection(url);
	}

	static void disconnect() throws SQLException {
		if (conn != null) {
			conn.close();
		}
	}
	
	static boolean existsManga(String id) throws SQLException {
		String s = "SELECT * FROM Mangas WHERE MangaId = \'" + id + "\';";
		ResultSet rs = query(s);
		return rs.next();
	}

	static boolean insertManga(String manga_id, String manga_title, String status, String description, String cover_art, String content_rating) throws SQLException {
		String s = "INSERT into Mangas (MangaId, Title, Status, Description, CoverArt, ContentRating) "
				+ "VALUES (\'" + manga_id + "\', \'" + manga_title + "\', \'" + status + "\', \'" + description + "\', \'" + cover_art + "\', \'" + content_rating + "\');";
		return execute(s);
	}
	
	// TODO
	static boolean insertMultipleManga() throws SQLException {
		String s = "";
		return execute(s);
	}

	static boolean existsChapter(String id) throws SQLException {
		String s = "SELECT * FROM Chapters WHERE ChapterId = \'" + id + "\';";
		ResultSet rs = query(s);
		return rs.next();
	}
	
	static boolean insertChapter(String chapter_id, String hash, String title, String volume, String chapter, String manga_id) throws SQLException {
		String s = "INSERT into Chapters (ChapterId, Hash, Title, Volume, Chapter, MangaId) VALUES "
				+ "(\'" + chapter_id + "\', \'" + hash + "\', \'" + title + "\', \'" + volume + "\', \'" + chapter + "\', \'" + manga_id + "\');";
		return execute(s);
	}
	
	/**
	 * Method to add every chapter of a given manga to the database. <br>
	 * Can take up to a few seconds depending on the amount of chapters of the manga.
	 */
	static boolean insertAllChapters(String manga_id, List<Chapter> chapters) throws SQLException {	
		String s1 = "INSERT into Chapters (ChapterId, Hash, Title, Volume, Chapter, MangaId, TranslatedLanguage, PublishAt, CreatedAt, UpdatedAt, ScanlationGroup) VALUES ";
		String s2 = "INSERT into Pages (FileName, ChapterId, DataSaver) VALUES ";
		
		int skipped = 0;
		
		for (int i = 0; i < chapters.size(); i++) {
			Chapter chapter = chapters.get(i);
			// Check if chapter is already in the database
			if (!MangaDexDatabase.existsChapter(chapter.id)) {			
				s1 += "(\'" + chapter.id + "\', \'" + chapter.hash + "\', \'" + chapter.title + "\', \'" + chapter.volume + "\', \'" + chapter.chapter + "\', \'" + manga_id + "\',"
						+ "\'" + chapter.translatedLanguage + "\', \'" + chapter.publishAt + "\', \'" + chapter.createdAt + "\', \'" + chapter.updatedAt + "\', \'" + chapter.scanlation_group + "\'), ";
				// Insert normal pages
				for (int j = 0; j < chapter.pages_data.size(); j++) {
					s2 += "(\'" + chapter.pages_data.get(j) + "\', \'" + chapter.id + "\', \'" + false + "\'), ";
				}
				// Insert lower res pages
				for (int j = 0; j < chapter.pages_dataSaver.size(); j++) {
					s2 += "(\'" + chapter.pages_dataSaver.get(j) + "\', \'" + chapter.id + "\', \'" + true + "\'), ";
				}
			} else {
				skipped++;
				continue;
			}
		}
		
		if (skipped == chapters.size()) {
			System.out.println("Up-to-date!");
			return false;
		}
		
		// Remove last ", "
		s1 = s1.substring(0, s1.length() - 2);
		s2 = s2.substring(0, s2.length() - 2);
		// Check if operation was successful
		boolean boo1 = execute(s1);
		boolean boo2 = execute(s2);
		return boo1 && boo2;
	}
	
	static boolean insertPages(String chapter_id, List<String> pages_data, List<String> pages_dataSaver) throws SQLException {
		String s = "INSERT into Pages (FileName, ChapterId, DataSaver) VALUES "
				+ "(\'" + pages_data.get(0) + "\', \'" + chapter_id + "\', \'" + false + "\')";
		for (int i = 1; i < pages_data.size(); i++) {
			s += ", (\'" + pages_data.get(i) + "\', \'" + chapter_id + "\', \'" + false + "\')";
		}
		for (int i = 0; i < pages_dataSaver.size(); i++) {
			s += ", (\'" + pages_dataSaver.get(i) + "\', \'" + chapter_id + "\', \'" + true + "\')";
		}
		s += ";";
		return execute(s);
	}
	
	static boolean existsTag(String id) throws SQLException {
		String s = "SELECT * FROM Tags WHERE TagId = \'" + id + "\';";
		ResultSet rs = query(s);
		return rs.next();
	}

	static boolean insertTag(String tag_id, String tag_name) throws SQLException {
		String s = "INSERT into Tags (TagId, TagName) VALUES (\'" + tag_id + "\', \'" + tag_name + "\');";
		return execute(s);
	}

	static String getTagId(String name) throws SQLException {
		String s = "SELECT TagId FROM Tags WHERE TagName = \'" + name + "\';";
		ResultSet rs = query(s);
		return rs.getString("TagId");
	}

	static boolean insertMangaTag(String manga_id, String tag_id) throws SQLException {
		String s = "INSERT into Manga_Tag (MangaId, TagId) VALUES (\'" + manga_id + "\', \'" + tag_id + "\');";
		return execute(s);
	}
	
	static boolean insertMultipleMangaTag(String manga_id, String[] tags) throws SQLException {
		String s = "INSERT into Manga_Tag (MangaId, TagId) VALUES (\'" + manga_id + "\', \'" + tags[0] + "\')";
		for (int i = 1; i < tags.length; i++) {
			s += ", (\'" + manga_id + "\', \'" + tags[i] + "\')";
		}
		s += ";";		
		return execute(s);
	}
	
	static boolean execute(String s) throws SQLException {
		Statement st = conn.createStatement();
		return st.execute(s);
	}

	static ResultSet query(String s) throws SQLException {
		Statement st = conn.createStatement();
		return st.executeQuery(s);
	}

}
