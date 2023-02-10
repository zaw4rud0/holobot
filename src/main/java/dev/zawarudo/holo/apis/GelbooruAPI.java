package dev.zawarudo.holo.apis;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO: Use Gelbooru Post to store information about a post instead of Json objects. This way
 *  an additional layer is created which increases maintainability.
 */
public final class GelbooruAPI {

	private static final String BASE_URL = "https://gelbooru.com/index.php?page=dapi&s=post&q=index&json=1&";
	
	private GelbooruAPI() {
	}
	
	/** List of Gelbooru tags that are banned */ 
	private static final List<String> BANNED = new ArrayList<>();

	public static void main(String[] args) throws IOException {
		System.out.println(getJsonObject(Rating.GENERAL, Sort.RANDOM, 1, "blahaj"));
	}

	public static JsonObject getJsonObject(Rating rating, Sort sort, int limit,	String tags) throws IOException {		
		String urlQueryString = BASE_URL + "limit=" + limit + "&tags=" + rating.getName() + sort.getName() + tags + "%20-" + String.join("%20-", BANNED);
		HttpURLConnection connection = (HttpURLConnection) new URL(urlQueryString).openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");
		InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
		BufferedReader reader = new BufferedReader(inputStreamReader);
		String s = reader.lines().collect(Collectors.joining("\n"));
		reader.close();
		inputStreamReader.close();
		connection.disconnect();
		JsonArray array = JsonParser.parseString(s).getAsJsonArray();
		return array.get(0).getAsJsonObject();
	}

	public static JsonArray getJsonArray(Rating rating, Sort sort, int limit, String tags) throws IOException {
		String urlQueryString = BASE_URL + "limit=" + limit + "&tags=" + rating.getName() + sort.getName() + tags;
		HttpURLConnection connection = (HttpURLConnection) new URL(urlQueryString).openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36");
		InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
		BufferedReader reader = new BufferedReader(inputStreamReader);
		String s = reader.lines().collect(Collectors.joining("\n"));
		reader.close();
		inputStreamReader.close();
		connection.disconnect();
		return JsonParser.parseString(s).getAsJsonObject().getAsJsonArray("post");
	}
	
	public enum Rating {
		ALL(""),
		GENERAL("rating:general%20-rating:questionable%20-rating:explicit%20"),
		QUESTIONABLE("rating:questionable%20"),
		EXPLICIT("rating:explicit%20");

		private final String name;

		Rating(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	/**
	 * Order of the posts
	 */
	public enum Sort {
		/** No order */
		RANDOM("sort:random%20"), 
		/** Least upvoted ones first */
		SCORE_ASC("sort:score:asc%20"),
		/** Most upvoted ones first */
		SCORE_DESC("sort:score:desc%20"),
		/** Oldest to newest */
		UPDATED_ASC("sort:updated:asc%20"),
		/** Newest to oldest */
		UPDATED_DESC("sort:updated:desc%20");

		private final String name;

		Sort(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}
}