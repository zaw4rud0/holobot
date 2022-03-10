package com.xharlock.holo.core;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.xharlock.holo.config.Config;
import com.xharlock.holo.utils.Reader;

public final class Bootstrap {

	public static Holo holo;
	/** The exact time at which the bot started up */
	public static long startupTime;

	private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

	private Bootstrap() {
	}

	public static void main(String[] args) {
		startupTime = System.currentTimeMillis();
		init();
		long totalTime = System.currentTimeMillis() - startupTime;

		if (logger.isInfoEnabled()) {
			logger.info(String.format("It took %s %d ms to load!", holo.getJDA().getSelfUser().getAsTag(), totalTime));
		}
	}

	static void init() {
		try {
			Config config = initializeConfig();
			holo = new Holo(config);
		} catch (IOException | LoginException | ParseException ex) {
			if (logger.isErrorEnabled()) {
				logger.error(ex.getMessage());
			}
			ex.printStackTrace();
		}
	}

	/**
	 * Deserializes the config file to a {@link Config} object.
	 */
	private static Config initializeConfig() throws IOException, ParseException {
		String json = Reader.readJSONObject("config.json").toJSONString();
		return new Gson().fromJson(json, Config.class);
	}
}