package com.xharlock.otakusenpai.misc;

import java.util.ArrayList;
import java.util.List;

import com.xharlock.otakusenpai.commands.core.Command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@SuppressWarnings("unused")
public class Action extends Command {

	private List<String> categories;
	
	public Action(String name) {
		super(name);
		
		
		this.categories = new ArrayList<>();
		this.loadCategories();
	}
	

	@Override
	public void onCommand(MessageReceivedEvent e) {
		// TODO Auto-generated method stub
		
	}

	public boolean check(MessageReceivedEvent e, String arg) {

		switch (arg.toLowerCase()) {
		case ("bite"):
			bite(e);
			return true;

		}
		return false;

	}
	
	private void loadCategories() {
		this.categories.add("bite");
		this.categories.add("blush");
		this.categories.add("cry");
		this.categories.add("cuddle");
		this.categories.add("highfive");
		this.categories.add("hug");
		this.categories.add("kiss");
		this.categories.add("lick");
		this.categories.add("nom");
		this.categories.add("pat");
		
	}

	private void bite(MessageReceivedEvent e) {

	}

	
	private void blush(MessageReceivedEvent e) {

	}

	private void cry(MessageReceivedEvent e) {

	}

	private void cuddle(MessageReceivedEvent e) {

	}

	private void highfive(MessageReceivedEvent e) {

	}

	private void hug(MessageReceivedEvent e) {

	}

	private void kiss(MessageReceivedEvent e) {

	}

	private void lick(MessageReceivedEvent e) {

	}

	private void nom(MessageReceivedEvent e) {

	}

	private void pat(MessageReceivedEvent e) {

	}

	private void poke(MessageReceivedEvent e) {

	}

	private void pout(MessageReceivedEvent e) {

	}

	private void punch(MessageReceivedEvent e) {

	}

	private void shrug(MessageReceivedEvent e) {

	}

	private void slap(MessageReceivedEvent e) {

	}

	private void sleepy(MessageReceivedEvent e) {

	}

	private void smile(MessageReceivedEvent e) {

	}

	private void stare(MessageReceivedEvent e) {

	}

	private void tickle(MessageReceivedEvent e) {

	}

	private void wag(MessageReceivedEvent e) {

	}

}
