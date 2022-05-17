package com.xharlock.holo.apis;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.xharlock.holo.exceptions.APIException;
import com.xharlock.holo.exceptions.InvalidRequestException;
import com.xharlock.holo.utils.HttpResponse;

import java.io.FileNotFoundException;
import java.io.IOException;

public final class XkcdAPI {

    public XkcdAPI() {
    }

    /**
     * Fetches the xkcd comic with the given number.
     */
    public static Comic getComic(int num) throws APIException, InvalidRequestException {
        String url = String.format(Endpoint.BY_NUMBER.getUrl(), num);
        JsonObject obj;
        try {
            obj = HttpResponse.getJsonObject(url);
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                throw new InvalidRequestException("Invalid issue number: " + num);
            }
            throw new APIException("Something went wrong with the API.");
        }
        return new Gson().fromJson(obj, Comic.class);
    }

    /**
     * Fetches the latest comic of xkcd.
     */
    public static Comic getLatest() throws APIException {
        String url = Endpoint.LATEST.getUrl();
        JsonObject obj;
        try {
            obj = HttpResponse.getJsonObject(url);
        } catch (IOException e) {
            throw new APIException("Something went wrong with the API.");
        }
        return new Gson().fromJson(obj, Comic.class);
    }

    private enum Endpoint {
        LATEST("info.0.json"),
        BY_NUMBER("%s/info.0.json");

        private static final String BASE_URL = "https://xkcd.com/";
        private final String endpoint;

        Endpoint(String endpoint){
            this.endpoint = endpoint;
        }

        public String getUrl() {
            return BASE_URL + endpoint;
        }
    }

    /**
     * Represents a single comic of xkcd.
     */
    public static class Comic implements Comparable<Comic> {
        @SerializedName("day")
        private int day;
        @SerializedName("month")
        private int month;
        @SerializedName("year")
        private int year;
        @SerializedName("num")
        private int num;
        @SerializedName("title")
        private String title;
        @SerializedName("alt")
        private String alt;
        @SerializedName("img")
        private String img;

        /**
         * Returns the year at which this comic was published.
         */
        public int getDay() {
            return day;
        }

        /**
         * Returns the month at which this comic was published.
         */
        public int getMonth() {
            return month;
        }

        /**
         * Returns the year at which this comic was published.
         */
        public int getYear() {
            return year;
        }

        /**
         * Returns the date at which the comic was published.
         */
        public String getDate() {
            return day + "/" + month + "/" + year;
        }

        /** Sets the date of the comic */
        public void setDate(int day, int month, int year) {
            this.day = day;
            this.month = month;
            this.year = year;
        }

        /** Returns the issue number of the comic */
        public int getIssueNr() {
            return num;
        }

        /** Sets the issue number of the comic */
        public void setIssueNr(int num) {
            this.num = num;
        }

        /** Returns the title of the comic */
        public String getTitle() {
            return title;
        }

        /** Sets the title of the comic */
        public void setTitle(String title) {
            this.title = title;
        }

        /** Returns the alt text of the comic */
        public String getAlt() {
            return alt;
        }

        /** Sets the alt text of the comic */
        public void setAlt(String alt) {
            this.alt = alt;
        }

        /** Returns the image Url of the comic */
        public String getImg() {
            return img;
        }

        /** Sets the image Url of the comic */
        public void setImg(String url) {
            this.img = url;
        }

        /**
         * Returns the permanent link of the comic
         */
        public String getUrl() {
            return "https://xkcd.com/" + num + "/";
        }

        /**
         * Returns the URL of the comic's explanation
         */
        public String getExplainedUrl() {
            return "https://www.explainxkcd.com/wiki/index.php/" + num;
        }

        /**
         * Returns this comic as a database entry.
         */
        public String getAsEntry() {
            String entry = "(%d, '%s', '%s', '%s', '%s', %d, %d, %d)";
            return String.format(entry, num, escape(title), escape(alt), img, getUrl(), day, month, year);
        }

        private String escape(String s) {
            return s.replace("'", "''");
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Comic && ((Comic)obj).getIssueNr() == this.num;
        }

        @Override
        public String toString() {
            return "xkcd " + num + ": " + title;
        }

        @Override
        public int compareTo(Comic other) {
            return Integer.compare(this.num, other.num);
        }
    }
}