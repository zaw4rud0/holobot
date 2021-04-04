package com.xharlock.otakusenpai.commands.owner;

import java.util.List;

import com.xharlock.otakusenpai.commands.core.Command;
import com.xharlock.otakusenpai.commands.core.CommandCategory;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ShutdownCmd extends Command {
	
	public ShutdownCmd(String name) {
        super(name);
        setDescription("(Owner-only) Use this command to shutdown the bot");
        setUsage(name);
        setAliases(List.of());
        setIsOwnerCommand(true);
        setIsGuildOnlyCommand(false);
        setCommandCategory(CommandCategory.OWNER);
    }
    
    @Override
    public void onCommand(MessageReceivedEvent e) {
        addSuccessReaction(e.getMessage());
        Runtime.getRuntime().exit(0);
    }

}
