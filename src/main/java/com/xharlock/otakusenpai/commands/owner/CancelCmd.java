package com.xharlock.otakusenpai.commands.owner;

import com.xharlock.otakusenpai.commands.core.Command;
import com.xharlock.otakusenpai.commands.core.CommandCategory;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CancelCmd extends Command {
	
    public CancelCmd(final String name) {
        super(name);
        setDescription("(Owner-only) Use this command to cancel all ongoing requests");
        setUsage(name);
        setIsOwnerCommand(true);
        setCommandCategory(CommandCategory.OWNER);
    }
    
    @Override
    public void onCommand(MessageReceivedEvent e) {
        this.addSuccessReaction(e.getMessage());
        e.getJDA().cancelRequests();
    }
}