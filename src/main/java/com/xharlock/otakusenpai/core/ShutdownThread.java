package com.xharlock.otakusenpai.core;

public class ShutdownThread extends Thread {

	@SuppressWarnings("unused")
	private OtakuSenpai otakuSenpai;
	
	public ShutdownThread(OtakuSenpai otakuSenpai) {
		this.otakuSenpai = otakuSenpai;
	}
	
	public void run() {
		System.out.println("\nSee you soon!\n");
	}
}
