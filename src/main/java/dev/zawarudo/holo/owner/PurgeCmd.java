package dev.zawarudo.holo.owner;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.annotations.Deactivated;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

@Deactivated
@Command(name = "purge",
        description = "Purges a given amount of messages of yours.",
        usage = "<amount>",
        ownerOnly = true,
        category = CommandCategory.OWNER)
public class PurgeCmd extends AbstractCommand {

    @Override
    public void onCommand(MessageReceivedEvent e) {

        // TODO: Implementation

        int amount = Integer.parseInt(args[0]);

        // Collect messages to be purged
        List<Message> messages = e.getChannel().getHistory().retrievePast(amount).complete().stream().filter(m -> m.getAuthor().equals(e.getAuthor())).toList();

        // Purge messages
        e.getChannel().purgeMessages(messages);
    }
}
