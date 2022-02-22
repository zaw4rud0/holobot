package com.xharlock.holo.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import net.dv8tion.jda.api.entities.Message;

/**
 * A thread that defines the behaviour of the bot when it shuts down
 */
public class ShutdownThread extends Thread {
	
	private Holo holo;
	
	public ShutdownThread(Holo holo) {
		this.holo = holo;
	}
	
	@Override
	public void run() {
		// Leaves all voice channels so bot doesn't get stuck after restarting
		holo.getJDA().getGuilds().stream().filter(g -> g.getSelfMember().getVoiceState().inAudioChannel()).forEach(g -> g.getAudioManager().closeAudioConnection());
		
		// Adds msgs to be deleted when the bot is online again
		List<Message> toDelete = new ArrayList<>();
		toDelete.addAll(holo.getPokemonSpawnManager().messages.values());
		writeToFile(toDelete);
	}
	
	private void writeToFile(List<Message> messages) {
		List<MessageToDelete> toDelete = new ArrayList<>();
		for (Message m : messages) {
			toDelete.add(new MessageToDelete(m));
		}
		String json = new Gson().toJson(toDelete);
		try {
			Files.writeString(new File("delete.json").toPath(), json);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}