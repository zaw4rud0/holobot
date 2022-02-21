package com.xharlock.holo.core;

public class ShutdownThread extends Thread {

	// TODO Define behaviour when the bot shuts down
	
	private Holo holo;
	
	public ShutdownThread(Holo holo) {
		this.holo = holo;
	}
	
	public Holo getHolo() {
		return holo;
	}
	
	public void run() {
		System.out.println("\nSee you soon!\n");
	}
}
