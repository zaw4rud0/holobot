package com.xharlock.holo.place;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DrawTxtCmd extends PlaceCommand {

	public DrawTxtCmd(String name) {
		super(name);
		setIsOwnerCommand(true);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		Attachment attach = e.getMessage().getAttachments().get(0);
        e.getMessage().delete().queue();
        List<String> lines = getContents(attach);
        
        for (final String s : lines) {
            e.getJDA().getTextChannelById(819966095070330950L).sendMessage(s).queue();
        }
        
        
	}
	
	private List<String> getContents(Attachment attachment) {
        List<String> s = new ArrayList<String>();
        Scanner scanner = null;
        try {
            scanner = new Scanner(attachment.retrieveInputStream().get());
        }
        catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }
        
        while (scanner.hasNextLine()) {
            s.add(scanner.nextLine());
        }
        
        scanner.close();
        return s;
    }

}
