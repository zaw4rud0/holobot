package com.xharlock.holo.core;

public class ShutdownThread extends Thread {

	@SuppressWarnings("unused")
	private Holo otakuSenpai;
	
	public ShutdownThread(Holo otakuSenpai) {
		this.otakuSenpai = otakuSenpai;
	}
	
	public void run() {
		System.out.println("\nSee you soon!\n");
	}
}
