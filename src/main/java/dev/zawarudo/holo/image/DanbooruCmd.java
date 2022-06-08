package dev.zawarudo.holo.image;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.annotations.Deactivated;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
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