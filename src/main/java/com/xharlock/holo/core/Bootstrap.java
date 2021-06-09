package com.xharlock.holo.core;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.xharlock.holo.config.Config;
import com.xharlock.holo.utils.JSONReader;

public class Bootstrap {
	
	public static Holo holo;
	public static long startup_time;
	
	private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);
	
	public static void main(String[] args) {
		startup_time = System.currentTimeMillis();
		init();
		long totalTime = System.currentTimeMillis() - startup_time;
		logger.info(String.format("It took %s %d ms to load!", holo.getJDA().getSelfUser().getAsTag(), totalTime));
	}

	static void init() {
		EventWaiter waiter = new EventWaiter();
		try {
			Config config = initializeConfig();
			holo = new Holo(config, waiter);
		} catch (IOException | ParseException | LoginException ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	/**
	 * Method to retrieve the credentials and configurations from a Json file and initialize a Config object
	 */
	private static Config initializeConfig() throws IOException, ParseException {
		JSONObject object = JSONReader.readJSONObject("config.json");
		Config config = new Config();
		config.setDiscordToken((String) object.get("token"));
		config.setOwnerId(Long.parseLong(object.get("owner_id").toString()));
		config.setKeyDeepAI((String) object.get("deepAI_token"));
		config.setDefaultPrefix((String) object.get("default_prefix"));
		config.setDefaultColor(Integer.parseInt(object.get("default_color").toString()));
		config.setVersion((String) object.get("version"));
		return config;
	}
}