package com.xharlock.holo.place;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ProtectCmd extends PlaceCommand {
	
	private static BufferedImage image;

	public ProtectCmd(String name) {
		super(name);
		setIsOwnerCommand(true);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {		
		try {
			image = ImageIO.read(new URL(e.getMessage().getAttachments().get(0).getUrl()));
			Place.mode = Mode.PROTECT;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static BufferedImage getImage() {
		return image;
	}
}
