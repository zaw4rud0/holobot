package dev.zawarudo.holo.core;

import com.google.gson.annotations.SerializedName;

/**
 * Class that stores the configuration for the bot.
 */
public class BotConfig {
    @SerializedName("token")
    private String botToken = "BOT_TOKEN";
    @SerializedName("owner_id")
    private long ownerId;

    // External settings and tokens
    @SerializedName("deepAI_token")
    private String deepAIKey = "DEEP_AI_KEY";
    @SerializedName("aoc_token")
    private String aocToken = "AOC_SESSION_KEY";
    @SerializedName("saucenao_token")
    private String saucenaoToken = "SAUCENAO_TOKEN";
    @SerializedName("github_token")
    private String githubToken = "GITHUB_TOKEN";

    // Minor bot properties
    @SerializedName("default_prefix")
    private String defaultPrefix = "<";
    @SerializedName("version")
    private String version = "1.0.0";

    /**
     * Retrieves the Discord bot token. Each bot has a unique token which should
     * never be shared with anyone.
     *
     * @return The bot token as a String.
     */
    public String getBotToken() {
        return botToken;
    }

    /**
     * Retrieves the Discord user id of the bot owner. This is used to check if a
     * user has permission to use certain commands of the bot.
     *
     * @return The Discord user id as a long.
     */
    public long getOwnerId() {
        return ownerId;
    }

    /**
     * Retrieves the DeepAI API key. The key is required to use the various endpoints
     * of the DeepAI API.
     *
     * @return The DeepAI API key as a String.
     */
    public String getKeyDeepAI() {
        return deepAIKey;
    }

    /**
     * Retrieves the API key of the Advent of Code (AoC) website. The key is required
     * to get data from there, such as the leaderboard.
     *
     * @return The AoC API key as a String.
     */
    public String getAoCToken() {
        return aocToken;
    }

    /**
     * Returns the token of SauceNAO.
     */
    public String getSaucenaoToken() {
        return saucenaoToken;
    }

    /**
     * Returns the GitHub token needed to connect with the GitHub repository of the bot.
     */
    public String getGitHubToken() {
        return githubToken;
    }

    /**
     * Returns the default prefix of the bot. The prefix is used to call commands.
     *
     * @return The default prefix as a String.
     */
    public String getDefaultPrefix() {
        return defaultPrefix;
    }

    /**
     * Sets a new default prefix for the bot. The prefix is used to call commands.
     *
     * @param prefix The new default prefix as a String.
     */
    public void setDefaultPrefix(String prefix) {
        this.defaultPrefix = prefix;
    }

    /**
     * Returns the current version of the bot.
     *
     * @return The current bot version as a String.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets a new version for the bot.
     *
     * @param version The new bot version as a String.
     */
    public void setVersion(String version) {
        this.version = version;
    }
}