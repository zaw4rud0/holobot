package com.xharlock.holo.anime;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import com.google.gson.JsonObject;

/** Class to scrape AnimePlanet for a given anime or manga */
// TODO Clean up and rewrite this decompiled code
public class AnimePlanetAPI {
	
	private static final List<String> NSFWTags;
    private static final List<String> ignoreH2s;
    
    static {
        NSFWTags = new ArrayList<String>(List.of("Animal Abuse", "BDSM", "Bullying", "Cannibalism", "Domestic Abuse", "Drug Use", "Emotional Abuse", "Explicit Sex", "Explicit Violence", "Fujoshi", "Fudanshi", "Incest", "Mature Themes", "Nudity", "Panty Shots", "Physical Abuse", "Prostitution", "Self-Harm", "Sexual Abuse", "Sexual Content", "Suicide", "Violence", "Animal Abuse,", "BDSM,", "Bullying,", "Cannibalism,", "Domestic Abuse,", "Drug Use,", "Emotional Abuse,", "Explicit Sex,", "Explicit Violence,", "Fujoshi,", "Fudanshi,", "Incest,", "Mature Themes,", "Nudity,", "Panty Shots,", "Physical Abuse,", "Prostitution,", "Self-Harm,", "Sexual Abuse,", "Sexual Content,", "Suicide,", "Violence,"));
        ignoreH2s = new ArrayList<String>(List.of("User Stats", "If you like this anime, you might like...", "Reviews", "Related anime", "Related manga", "Characters", "Staff", "Discussions", "Custom lists", "You've Earned Badges!", "Plan to watch this anime?"));
    }
    
    public static Anime getAnime(String animeName) {
        animeName = replaceCharacters(animeName);
        final String url = "https://www.anime-planet.com/anime/" + animeName;
        final Document doc = getDoc(url);
        if (doc == null) {
            return null;
        }
        final String title = unescapeCharacterEntities(doc.getElementsByTag("h1").first().html());
        if (title.contains("You searched")) {
            return null;
        }
        String alternativeTitle = unescapeCharacterEntities(doc.getElementsByTag("h2").first().html());
        if (AnimePlanetAPI.ignoreH2s.contains(alternativeTitle)) {
            alternativeTitle = null;
        }
        else {
            alternativeTitle = alternativeTitle.substring(11);
        }
        final String image = doc.select("img").first().absUrl("src");
        final String description = unescapeCharacterEntities(cutStringIfTooBig(doc.getElementsByTag("p").first().html()));
        final String year = doc.select("div.pure-1.md-1-5 > span.iconYear").html();
        final String[] raw = doc.select("div.pure-1.md-1-5 > a").html().split("\n");
        String studio = null;
        AnimeSeason season = null;
        if (raw[0].contains("Winter") || raw[0].contains("Spring") || raw[0].contains("Summer") || raw[0].contains("Fall")) {
            season = new AnimeSeason(raw[0]);
        }
        else if (!raw[0].equals("") && !raw[0].equals(" ") && !raw[0].equals("\n")) {
            studio = raw[0];
        }
        if (raw.length == 2) {
            if (raw[1].contains("Winter") || raw[1].contains("Spring") || raw[1].contains("Summer") || raw[1].contains("Fall")) {
                season = new AnimeSeason(raw[1]);
            }
        }
        else if (raw.length > 2 && (raw[2].contains("Winter") || raw[2].contains("Spring") || raw[2].contains("Summer") || raw[2].contains("Fall"))) {
            season = new AnimeSeason(raw[2]);
        }
        final String[] tags = removeBadTags(doc.getElementsByAttribute("data-tooltip-entry").html().split("\n"));
        final String episodesRaw = doc.getElementsByTag("span").first().html();
        String episodes;
        if (episodesRaw.contains("TV") && episodesRaw.length() <= 2) {
            episodes = "TBA";
        }
        else if (episodesRaw.contains("Movie")) {
            episodes = "Movie";
        }
        else if (episodesRaw.contains("Web")) {
            episodes = episodesRaw.substring(5, episodesRaw.length() - 1);
        }
        else if (episodesRaw.contains("OVA")) {
            episodes = "OVA";
        }
        else if (episodesRaw.contains("Other") || episodesRaw.contains("Special")) {
            episodes = "Special";
        }
        else if (episodesRaw.contains("min")) {
            episodes = episodesRaw.substring(4, episodesRaw.length() - 9);
        }
        else if (episodesRaw.contains("TV")) {
            episodes = episodesRaw.substring(4, episodesRaw.length() - 5);
        }
        else {
            episodes = "Special";
        }
        final Anime anime = new Anime(title, alternativeTitle, image, episodes, year, season, description, tags, studio, url);
        return anime;
    }
    
    public JsonObject getAnimeObject(final String name) {
        return null;
    }
    
    public static Manga getManga(String mangaName) {
        mangaName = replaceCharacters(mangaName);
        final String url = "https://www.anime-planet.com/manga/" + mangaName;
        final Document doc = getDoc(url);
        if (doc == null) {
            return null;
        }
        final String title = unescapeCharacterEntities(doc.getElementsByTag("h1").first().html());
        if (title.contains("You searched")) {
            return null;
        }
        final String image = doc.select("img").first().absUrl("src");
        final String description = unescapeCharacterEntities(cutStringIfTooBig(doc.getElementsByTag("p").first().html()));
        final String year = doc.select("div.pure-1.md-1-5 > span.iconYear").html();
        final String[] tags = removeBadTags(doc.getElementsByAttribute("data-tooltip-entry").html().split("\n"));
        final String chapters = doc.select("section.pure-g.entryBar > div.pure-1.md-1-5").first().html().replace("; ", "\n");
        final Manga manga = new Manga(title, chapters, image, year, description, tags, url);
        return manga;
    }
    
    public JsonObject getMangaObject(final String name) {
        return null;
    }
    
    private static Document getDoc(final String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36").get();
        }
        catch (Exception e) {
            System.out.println("\nSomething went wrong while scraping anime-planet.com!\n");
            return null;
        }
        return doc;
    }
    
    private static String replaceCharacters(final String in) {
        return in.toLowerCase().replace(" ", "-").replace("&", "and").replace("'", "").replace(";", "-").replace(":-", "-").replace(":", "-").replace("\u00e4", "a").replace("!", "").replace("?", "");
    }
    
    private static String unescapeCharacterEntities(String in) {
        int less_pos = in.indexOf("<a");
        int link_start = in.indexOf("href=\"");
        for (int word_start = in.indexOf("\">"), end_pos = in.indexOf("</a>"); less_pos >= 0 && word_start >= 0 && end_pos >= 0; less_pos = in.indexOf("<a"), link_start = in.indexOf("href=\""), word_start = in.indexOf("\">"), end_pos = in.indexOf("</a>")) {
            final String word = in.substring(word_start + 2, end_pos);
            final String link = in.substring(link_start + 6, in.indexOf(34, link_start + 7));
            in = String.valueOf(in.substring(0, less_pos)) + "[" + word + "](" + link + ")" + in.substring(end_pos + 4);
        }
        String unescapedString = Parser.unescapeEntities(in, false);
        unescapedString = unescapedString.replace("<em>", "*").replace("</em>", "*").replace("<i>", "*").replace("</i>", "*").replace("<b>", "**").replace("</b>", "**").replace("&nbsp;", " ");
        return unescapedString;
    }
    
    private static String cutStringIfTooBig(final String in) {
        int counter = 0;
        String cutString = "";
        for (int i = 0; i < in.length(); ++i) {
            if (counter + 3 == 1024) {
                cutString = String.valueOf(cutString) + "...";
                return cutString;
            }
            cutString = String.valueOf(cutString) + in.charAt(i);
            ++counter;
        }
        return in;
    }
    
    private static String[] removeBadTags(final String[] tags) {
        final List<String> removeHelper = new ArrayList<String>();
        for (final String new_string : tags) {
            if (new_string != null && !AnimePlanetAPI.NSFWTags.contains(new_string)) {
                removeHelper.add(new_string);
            }
        }
        return removeHelper.toArray(new String[removeHelper.size()]);
    }
	
}
