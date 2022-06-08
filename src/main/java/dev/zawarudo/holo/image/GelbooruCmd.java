package dev.zawarudo.holo.image;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.annotations.Deactivated;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import dev.zawarudo.holo.misc.EmbedColor;
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