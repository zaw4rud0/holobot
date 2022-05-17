package com.xharlock.holo.image;

import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.annotations.Deactivated;
import com.xharlock.holo.core.AbstractCommand;
import com.xharlock.holo.core.CommandCategory;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Deactivated
@Command(name = "cat",
        description = "Sends an image of a cat.",
        category = CommandCategory.IMAGE)
public class CatCmd extends AbstractCommand {

    @Override
    public void onCommand(MessageReceivedEvent event) {
        event.getMessage().reply("This command is not yet implemented.").queue();
    }
}