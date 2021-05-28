package com.xharlock.otakusenpai.core;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.xharlock.otakusenpai.config.Config;
import com.xharlock.otakusenpai.utils.JSONReader;

public class Bootstrap {
	public static OtakuSenpai otakuSenpai;
	public static long startup_time;
	
	public static void main(String[] args) {
		startup_time = System.currentTimeMillis();
		init();
		long totalTime = System.currentTimeMillis() - startup_time;
		System.out.println(String.format("It took %s %d ms to load!", otakuSenpai.getJDA().getSelfUser().getAsTag(), totalTime));
	}

	static void init() {
		EventWaiter waiter = new EventWaiter();		
		try {
			Config config = initializeConfig();
			otakuSenpai = new OtakuSenpai(config, waiter);
		} catch (IOException | ParseException | LoginException ex) {
			ex.printStackTrace();
		}
	}
	
	private static Config initializeConfig() throws IOException, ParseException {
		JSONObject object = JSONReader.readJSONObject("config.json");
		Config config = new Config();
		config.setDiscordToken((String) object.get("token"));
		config.setOwnerId(Long.parseLong(object.get("owner_id").toString()));
		config.setKeyDeepAI((String) object.get("deepAI_token"));
		config.setPrefix((String) object.get("default_prefix"));
		config.setColor(Integer.parseInt(object.get("default_color").toString()));
		config.setVersion((String) object.get("version"));
		return config;
	}
}
