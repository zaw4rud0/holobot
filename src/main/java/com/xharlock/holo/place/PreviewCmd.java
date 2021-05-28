package com.xharlock.holo.place;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;

import com.xharlock.holo.utils.BufferedImageOperations;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.AttachmentOption;

public class PreviewCmd extends PlaceCommand {

	public PreviewCmd(String name) {
		super(name);
		setDescription(
				"Use this command to see what the result of a text file with command lines will look like. If desired, one can also provide an image link to see how the new image looks on the canvas. Always provide a text file as attachment.");
		setUsage("preview [img link]");

	}

	@Override
	public void onCommand(MessageReceivedEvent e) {

		if (e.getMessage().getAttachments().isEmpty()) {
			e.getChannel().sendMessage("No attachment provided!").queue();
			return;
		}

		if (!e.getMessage().getAttachments().get(0).getFileExtension().equals("txt")) {
			e.getChannel().sendMessage("Attachment has wrong extension!").queue();
			return;
		}

		BufferedImage result = null;

		if (args.length == 1) {
			try {
				result = ImageIO.read(new URL(args[0]));
			} catch (MalformedURLException e2) {
				e2.printStackTrace();
			} catch (IOException e3) {
				e3.printStackTrace();
			}

			result = BufferedImageOperations.resize(result, 1000, 1000);
			result = BufferedImageOperations.toGrayScale(result);
		} else {
			if (args.length != 0) {
				e.getChannel().sendMessage("Wrong usage!").queue();
				return;
			}
			result = new BufferedImage(1000, 1000, 2);
		}

		List<String> lines = getContents(e.getMessage().getAttachments().get(0));
		for (String s : lines) {
			Scanner scanner = new Scanner(s.substring(16));
			int x = scanner.nextInt();
			int y = scanner.nextInt();
			String hex = scanner.next();
			try {
				result.setRGB(x, y, Color.decode(hex).getRGB());
			} catch (ArrayIndexOutOfBoundsException ex) {
				e.getChannel().sendMessage(
						"Your file contains some coordinates that are out of bounds <:monka:747783378207768626>")
						.queue();
				scanner.close();
				return;
			} finally {
				scanner.close();
			}
		}
		try {
			e.getChannel().sendFile(BufferedImageOperations.toInputStream(result), "preview.png", new AttachmentOption[0])
					.queue();
		} catch (IOException e4) {
			e4.printStackTrace();
		}

	}

	private List<String> getContents(Message.Attachment attachment) {

		List<String> s = new ArrayList<String>();
		Scanner scanner = null;
		try {
			scanner = new Scanner(attachment.retrieveInputStream().get());
		} catch (InterruptedException | ExecutionException ex) {
			ex.printStackTrace();
		}

		while (scanner.hasNextLine()) {
			s.add(scanner.nextLine());
		}
		scanner.close();
		return s;
	}

}
