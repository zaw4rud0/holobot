package dev.zawarudo.holo.image;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.annotations.Deactivated;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
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