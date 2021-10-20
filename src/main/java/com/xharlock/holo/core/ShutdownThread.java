package com.xharlock.holo.core;

public class ShutdownThread extends Thread {

	// WIP
	
	@SuppressWarnings("unused")
	private Holo holo;
	
	public ShutdownThread(Holo holo) {
		this.holo = holo;
	}
	
	public void run() {
		System.out.println("\nSee you soon!\n");
	}
}
