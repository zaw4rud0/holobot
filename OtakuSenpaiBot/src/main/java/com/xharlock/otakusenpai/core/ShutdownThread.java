package com.xharlock.otakusenpai.core;

public class ShutdownThread extends Thread {

	@SuppressWarnings("unused")
	private OtakuSenpai otakuSenpai;
	
	public ShutdownThread(OtakuSenpai otakuSenpai) {
		this.otakuSenpai = otakuSenpai;
	}
	
	public void run() {
		System.out.println("\nI'm going to sleep but I will be back soon!\n");
	}
}
