package com.xharlock.holo.place;

import com.xharlock.holo.commands.core.Command;
import com.xharlock.holo.commands.core.CommandCategory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BullyCmd extends Command {

	public BullyCmd(String name) {
		super(name);
		setIsOwnerCommand(true);
		setCommandCategory(CommandCategory.PLACE);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		
		e.getMessage().delete().queue();
		EmbedBuilder builder = new EmbedBuilder();
		
		if (args[0].equals("restore")) {
			Place.mode = Mode.BULLY_RESTORE;
			Place.target = e.getJDA().getUserById(args[1]);
			builder.setTitle("Bully");
			builder.setDescription("Now bullying " + e.getJDA().getUserById(args[1]).getAsMention() + "(" + args[1] + ") whilst restoring the pixels");
			sendEmbed(e, builder, false);
		}
		
		else if (args[0].equals("background")) {
			Place.mode = Mode.BULLY_BACKGROUND;
			Place.target = e.getJDA().getUserById(args[1]);
		}
		
		else if (args[0].equals("random")) {
			Place.mode = Mode.BULLY_RANDOM;
			Place.target = e.getJDA().getUserById(args[1]);
		}
		
		else if (args[0].equals("none")) {
			Place.mode = Mode.NONE;
			Place.target = null;
		}
		
	}

}
