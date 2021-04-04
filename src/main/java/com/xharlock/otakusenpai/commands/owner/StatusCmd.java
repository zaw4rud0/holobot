package com.xharlock.otakusenpai.commands.owner;

import java.util.Arrays;
import java.util.List;

import com.xharlock.otakusenpai.commands.core.Command;
import com.xharlock.otakusenpai.commands.core.CommandCategory;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class StatusCmd extends Command {

	public StatusCmd(String name) {
		super(name);
		setDescription("(Owner-only) Use this command to set the status of the bot");
		setUsage(name);
		setAliases(List.of());
        setIsOwnerCommand(true);
        setIsGuildOnlyCommand(false);
        setCommandCategory(CommandCategory.OWNER);
	}

	@Override
	public void onCommand(MessageReceivedEvent e) {
		e.getMessage().delete().queue();
		
        if (args.length == 0) {
            int guilds = e.getJDA().getGuilds().size();
            int users = e.getJDA().getUsers().size();
            e.getJDA().getPresence().setActivity(Activity.listening(users + " users on " + guilds + " servers"));
            return;
        }
        if (args[0].equals("default")) {
            e.getJDA().getPresence().setActivity(Activity.watching(getGuildPrefix(e.getGuild()) + "help"));
            return;
        }
        
        String status = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        
        if (args[0].equals("listening")) {
            e.getJDA().getPresence().setActivity(Activity.listening(status));
            return;
        }
        if (args[0].equals("playing")) {
            e.getJDA().getPresence().setActivity(Activity.playing(status));
            return;
        }
        if (args[0].equals("watching")) {
            e.getJDA().getPresence().setActivity(Activity.watching(status));
            return;
        }
        if (args[0].equals("competing")) {
            e.getJDA().getPresence().setActivity(Activity.competing(status));
            return;
        }
	}

}
