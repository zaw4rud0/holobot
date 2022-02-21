package com.xharlock.holo.image;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import com.google.gson.JsonObject;
import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;
import com.xharlock.holo.utils.HttpResponse;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ActionCmd extends Command {

	public ActionCmd(String name) {
		super(name);
		setIsGuildOnlyCommand(true);
		setCommandCategory(CommandCategory.ACTION);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {		
		try {
			System.out.println(getUrl(Action.BAKA));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private String getUrl(Action action) throws IOException {
		JsonObject obj = HttpResponse.getJsonObject(action.getUrl());
		return obj.get("url").getAsString();
	}

	public enum Action {
		BAKA("baka", true, List.of("https://nekos.best/api/v1/baka", "https://nekos.life/api/v2/img/baka")),
		BITE("bite", true, List.of("https://nekos.best/api/v1/bite")),
		BLUSH("blush", false, List.of("https://nekos.best/api/v1/blush")),
		CRY("cry", false, List.of("https://nekos.best/api/v1/cry")),
		CUDDLE("cuddle", true, List.of("https://nekos.best/api/v1/cuddle", "https://nekos.life/api/v2/img/cuddle")),
		DANCE("dance", false, List.of("https://nekos.best/api/v1/dance")),
		FACEPALM("facepalm", false, List.of("https://nekos.best/api/v1/facepalm")),
		FEED("feed", true, List.of("https://nekos.best/api/v1/feed", "https://nekos.life/api/v2/img/feed")),
		HIGHFIVE("highfive", true, List.of("https://nekos.best/api/v1/highfive")),
		HUG("hug", true, List.of("https://nekos.best/api/v1/hug", "https://nekos.life/api/v2/img/hug")),
		KISS("kiss", true, List.of("https://nekos.best/api/v1/kiss", "https://nekos.life/api/v2/img/kiss")),
		LAUGH("laugh", false, List.of("https://nekos.best/api/v1/laugh")),
		PAT("pat", true, List.of("https://nekos.best/api/v1/pat", "https://nekos.life/api/v2/img/pat")),
		POKE("poke", true, List.of("https://nekos.best/api/v1/poke", "https://nekos.life/api/v2/img/poke")),
		POUT("pout", false, List.of("https://nekos.best/api/v1/pout")),
		SHRUG("shrug", false, List.of("https://nekos.best/api/v1/shrug")),
		SLAP("slap", true, List.of("https://nekos.best/api/v1/slap", "https://nekos.life/api/v2/img/slap")),
		SLEEP("sleep", false, List.of("https://nekos.best/api/v1/sleep")),
		SMILE("smile", false, List.of("https://nekos.best/api/v1/smile")),
		SMUG("smug", false, List.of("https://nekos.best/api/v1/smug", "https://nekos.life/api/v2/img/smug")),
		STARE("stare", true, List.of("https://nekos.best/api/v1/stare")),
		THINK("think", false, List.of("https://nekos.best/api/v1/think")),
		THUMBSUP("thumbsup", false, List.of("https://nekos.best/api/v1/thumbsup")),
		TICKLE("tickle", true, List.of("https://nekos.best/api/v1/tickle", "https://nekos.life/api/v2/img/tickle")),
		WAVE("wave", false, List.of("https://nekos.best/api/v1/wave")),
		WINK("wink", false, List.of("https://nekos.best/api/v1/wink"));

		private String name;
		private boolean directed;
		private List<String> urls;

		Action(String name, boolean directed, List<String> urls) {
			this.urls = urls;
		}

		public String getName() {
			return name;
		}

		public boolean isDirected() {
			return directed;
		}

		public String getUrl() {
			return urls.get(new Random().nextInt(urls.size()));
		}
	}
}
