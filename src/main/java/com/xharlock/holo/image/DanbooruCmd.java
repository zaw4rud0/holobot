package com.xharlock.holo.image;

import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.annotations.Deactivated;
import com.xharlock.holo.core.AbstractCommand;
import com.xharlock.holo.core.CommandCategory;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Deactivated
@Command(name = "danbooru",
        description = "Retrieves an image from Danbooru.",
        usage = "<tag>",
        isNSFW = true,
        category = CommandCategory.IMAGE)
public class DanbooruCmd extends AbstractCommand {

    @Override
    public void onCommand(MessageReceivedEvent e) {
        e.getMessage().reply("This command is not yet implemented.").queue();
    }
}