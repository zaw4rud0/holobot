package com.xharlock.holo.image;

import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonObject;
import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;
import com.xharlock.holo.utils.HttpResponse;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ActionCmd extends Command {

	private Map<String, Action> actions;
	
	public ActionCmd(String name) {
		super(name);
		setDescription("Use this command to send an action GIF");
		setUsage(name + " [<action> | list]");
		setExample(name + " blush");
		setIsGuildOnlyCommand(true);
		setEmbedColor(Color.LIGHT_GRAY);
		setCommandCategory(CommandCategory.IMAGE);
		
		actions = new LinkedHashMap<>();
		initializeActions();
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		EmbedBuilder builder = new EmbedBuilder();
		
		// Show a list of available actions
		if (args.length == 0 || args[0].equals("list")) {
			deleteInvoke(e);
			builder.setTitle("List of Actions");
			builder.setDescription(getActionsAsString());
			sendEmbed(e, builder, 1, TimeUnit.MINUTES, true, embedColor);
		} 
		
		// Call specific action
		else if (isAction(args[0])) {
			Action action = actions.get(args[0]);
			args = Arrays.copyOfRange(args, 1, args.length);
			displayAction(e, action);
		} 
		
		// Unknown action
		else {
			builder.setTitle("Error");
			builder.setDescription("Couldn't find this action. Use `" + getPrefix(e) + "action list` to see all available actions");
			sendEmbed(e, builder, 1, TimeUnit.MINUTES, true, embedColor);
		}
	}
	
	public void displayAction(MessageReceivedEvent e, Action action) {
		deleteInvoke(e);
		EmbedBuilder builder = new EmbedBuilder();
		
		String url = null;
		
		try {
			url = action.getLink();
		} catch (IOException ex) {
			builder.setTitle("Error");
			builder.setDescription("Something went wrong while fetching an image");
			sendEmbed(e, builder, 15, TimeUnit.SECONDS, true, embedColor);
			return;
		}
		
		String mention = "nothing";
		
		if (args.length != 0) {
			if (!e.getMessage().getMentionedMembers().isEmpty()) {
				mention = e.getMessage().getMentionedMembers().get(0).getEffectiveName();
			} else {
				mention = String.join(" ", args);
			}
		}
		
		String title = action.getSentence().replace("{s}", e.getMember().getEffectiveName()).replace("{u}", mention);
		builder.setTitle(title);
		builder.setImage(url);
		
		sendEmbed(e, builder, false, embedColor);
	}

	/**
	 * Checks if a given string is an action
	 */
	public boolean isAction(String name) {
		return actions.keySet().contains(name);
	}
	
	public Action getAction(String name) {
		return actions.get(name);
	}
	
	private void initializeActions() {
		for (Action action : Action.values()) {
			actions.put(action.name, action);
		}
	}
	
	private String getActionsAsString() {
		return actions.keySet().toString().replace("]", "`").replace("[", "`").replace(",", "`").replace(" ", ", `");
	}
	
	public enum Action {
		BAKA("baka", "{s} calls {u} a baka", true, List.of("https://nekos.best/api/v1/baka", "https://nekos.life/api/v2/img/baka")),
		BITE("bite", "{s} bites {u}", true, List.of("https://nekos.best/api/v1/bite")),
		BLUSH("blush", "{s} blushes", false, List.of("https://nekos.best/api/v1/blush")),
		BORED("bored", "{s} is bored", false, List.of("https://nekos.best/api/v1/bored")),
		CRY("cry", "{s} cries", false, List.of("https://nekos.best/api/v1/cry")),
		CUDDLE("cuddle", "{s} cuddles {u}", true, List.of("https://nekos.best/api/v1/cuddle", "https://nekos.life/api/v2/img/cuddle")),
		DANCE("dance", "{s} dances", false, List.of("https://nekos.best/api/v1/dance")),
		FACEPALM("facepalm", "{s} face-palms", false, List.of("https://nekos.best/api/v1/facepalm")),
		FEED("feed", "{s} feeds {u}", true, List.of("https://nekos.best/api/v1/feed", "https://nekos.life/api/v2/img/feed")),
		HAPPY("happy", "{s} is happy", false, List.of("https://nekos.best/api/v1/happy")),
		HIGHFIVE("highfive", "{s} highfives {u}", true, List.of("https://nekos.best/api/v1/highfive")),
		HUG("hug", "{s} hugs {u}", true, List.of("https://nekos.best/api/v1/hug", "https://nekos.life/api/v2/img/hug")),
		KISS("kiss", "{s} kisses {u}", true, List.of("https://nekos.best/api/v1/kiss", "https://nekos.life/api/v2/img/kiss")),
		LAUGH("laugh", "{s} laughs", false, List.of("https://nekos.best/api/v1/laugh")),
		PAT("pat", "{s} pats {u}", true, List.of("https://nekos.best/api/v1/pat", "https://nekos.life/api/v2/img/pat")),
		POKE("poke", "{s} pokes {u}", true, List.of("https://nekos.best/api/v1/poke", "https://nekos.life/api/v2/img/poke")),
		POUT("pout", "{s} pouts", false, List.of("https://nekos.best/api/v1/pout")),
		SHRUG("shrug", "{s} shrugs", false, List.of("https://nekos.best/api/v1/shrug")),
		SLAP("slap", "{s} slaps {u}", true, List.of("https://nekos.best/api/v1/slap", "https://nekos.life/api/v2/img/slap")),
		SLEEP("sleep", "{s} sleeps", false, List.of("https://nekos.best/api/v1/sleep")),
		SMILE("smile", "{s} smiles", false, List.of("https://nekos.best/api/v1/smile")),
		SMUG("smug", "{s} is smug", false, List.of("https://nekos.best/api/v1/smug", "https://nekos.life/api/v2/img/smug")),
		STARE("stare", "{s} stares at {u}", true, List.of("https://nekos.best/api/v1/stare")),
		THINK("think", "{s} thinks", false, List.of("https://nekos.best/api/v1/think")),
		THUMBSUP("thumbsup", "{s} gives a thumbs-up", false, List.of("https://nekos.best/api/v1/thumbsup")),
		TICKLE("tickle", "{s} tickles {u}", true, List.of("https://nekos.best/api/v1/tickle", "https://nekos.life/api/v2/img/tickle")),
		WAVE("wave", "{s} waves", false, List.of("https://nekos.best/api/v1/wave")),
		WINK("wink", "{s} winks", false, List.of("https://nekos.best/api/v1/wink"));

		private String name;
		private String sentence;
		private boolean directed;
		private List<String> urls;

		Action(String name, String sentence, boolean directed, List<String> urls) {
			this.name = name;
			this.sentence = sentence;
			this.directed = directed;
			this.urls = urls;
		}

		public String getName() {
			return name;
		}

		public String getSentence() {
			return sentence;
		}
		
		public boolean isDirected() {
			return directed;
		}

		public List<String> getUrls() {
			return urls;
		}
		
		/**
		 * Returns a link to an action gif
		 */
		public String getLink() throws IOException {
			JsonObject obj = HttpResponse.getJsonObject(urls.get(new Random().nextInt(urls.size())));
			return obj.get("url").getAsString();
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
}