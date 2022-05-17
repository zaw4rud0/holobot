package com.xharlock.holo.image;

import com.xharlock.holo.annotations.Command;
import com.xharlock.holo.annotations.Deactivated;
import com.xharlock.holo.core.AbstractCommand;
import com.xharlock.holo.core.CommandCategory;
import com.xharlock.holo.misc.EmbedColor;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Deactivated
@Command(name = "gelbooru",
        description = "Retrieves an image from [Gelbooru](https://gelbooru.com/).",
        usage = "<tag>",
        embedColor = EmbedColor.GELBOORU,
        isNSFW = true,
        category = CommandCategory.IMAGE)
public class GelbooruCmd extends AbstractCommand {

    @Override
    public void onCommand(MessageReceivedEvent event) {
        event.getMessage().reply("This command is not yet implemented.").queue();
    }
}