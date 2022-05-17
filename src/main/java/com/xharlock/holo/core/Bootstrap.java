package com.xharlock.holo.core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xharlock.holo.config.BotConfig;
import com.xharlock.holo.utils.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;

/**
 * Main class of the application.
 */
public final class Bootstrap {

	public static Holo holo;
	/** The exact time at which the bot started up */
	public static long startupTime;

	private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

	private Bootstrap() {
	}

	public static void main(String[] args) {
		init();
	}

	private static void init() {
		startupTime = System.currentTimeMillis();

		try {
			BotConfig botConfig = initializeConfig();
			holo = new Holo(botConfig);
		} catch (IOException | LoginException ex) {
			if (logger.isErrorEnabled()) {
				logger.error(ex.getMessage());
			}
			ex.printStackTrace();
		}

		long totalTime = System.currentTimeMillis() - startupTime;

		if (logger.isInfoEnabled()) {
			logger.info(String.format("It took %s %d ms to load!", holo.getJDA().getSelfUser().getAsTag(), totalTime));
		}
	}

	/**
	 * Deserializes the config file to a {@link BotConfig} object.
	 */
	private static BotConfig initializeConfig() throws IOException {
		JsonObject obj = Reader.readJsonObject("config.json");
		return new Gson().fromJson(obj, BotConfig.class);
	}

	/**
	 * Restarts the bot.
	 */
	public static void restart() {
		if (logger.isInfoEnabled()) {
			logger.info("Restarting...");
		}
		init();
	}
}