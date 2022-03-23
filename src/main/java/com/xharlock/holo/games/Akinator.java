package com.xharlock.holo.games;

import javax.annotation.Nonnull;

import com.markozajc.akiwrapper.Akiwrapper;
import com.markozajc.akiwrapper.AkiwrapperBuilder;
import com.markozajc.akiwrapper.core.entities.Server.GuessType;
import com.markozajc.akiwrapper.core.exceptions.ServerNotFoundException;
import com.xharlock.holo.exceptions.APIException;

/**
 * An instance of Akinator with its sprites and questions/answers
 */
public class Akinator {
	
	private Akiwrapper instance;
	
	public Akinator() throws APIException {
		try {
			instance = new AkiwrapperBuilder().build();
		} catch (ServerNotFoundException e) {
			throw new APIException();
		}
	}
	
	public Akinator(@Nonnull GuessType type) throws APIException {
		try {
			instance = new AkiwrapperBuilder().setGuessType(type).build();
		} catch (ServerNotFoundException e) {
			throw new APIException();
		}
	}
	
	public Akiwrapper getInstance() {
		return instance;
	}
	
	/**
	 * The sprite of Akinator. Each sprite has a different facial expression or gesture or both.
	 */
	public enum Sprite {
		DEFAULT("https://media.discordapp.net/attachments/824916413139124254/824917438062919740/akinator_default.png"),
		START("https://media.discordapp.net/attachments/824916413139124254/824927512827527178/akinator_start.png"),	
		THINKING_1("https://media.discordapp.net/attachments/824916413139124254/824917445125865472/akinator_thinking_1.png"),
		THINKING_2("https://media.discordapp.net/attachments/824916413139124254/824917445629575168/akinator_thinking_2.png"),
		THINKING_3("https://media.discordapp.net/attachments/824916413139124254/824917447999750144/akinator_thinking_3.png"),
		THINKING_4("https://media.discordapp.net/attachments/824916413139124254/824927627483545610/akinator_thinking_4.png"),
		THINKING_5("https://media.discordapp.net/attachments/824916413139124254/824927631589376010/akinator_thinking_5.png"),
		THINKING_6("https://media.discordapp.net/attachments/824916413139124254/824927633992581130/akinator_thinking_6.png"),
		SHOCKED("https://media.discordapp.net/attachments/824916413139124254/824927453982097418/akinator_shocked.png"),
		DEFEAT("https://media.discordapp.net/attachments/824916413139124254/824917439242043412/akinator_defeat.png"),
		GUESSING("https://media.discordapp.net/attachments/824916413139124254/824917441557299220/akinator_guessing.png"),
		VICTORY("https://media.discordapp.net/attachments/824916413139124254/824917454647721984/akinator_victory.png"),
		CANCEL("https://media.discordapp.net/attachments/824916413139124254/824927268140089415/akinator_cancel.png");
		
		private String url;
		
		Sprite(String url) {
			this.url = url;
		}
		
		public String getUrl() {
			return url;
		}
	}
}