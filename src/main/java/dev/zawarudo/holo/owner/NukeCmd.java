package dev.zawarudo.holo.owner;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;

@Command(name = "nuke",
        description = "Deletes a given amount of messages indiscriminately within the channel.",
        usage = "<amount>",
        ownerOnly = true,
        category = CommandCategory.OWNER)
public class NukeCmd extends AbstractCommand {

    @Override
    public void onCommand(MessageReceivedEvent e) {
        int amount;

        try {
            amount = Integer.parseInt(args[0]);
            if (amount < 2) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            return;
        }

        int remaining = amount;
        List<Message> messagesToNuke = new ArrayList<>();

        while (remaining > 0) {
            if (remaining > 100) {
                messagesToNuke.addAll(e.getTextChannel().getHistory().retrievePast(100).complete());
                e.getTextChannel().deleteMessages(messagesToNuke).queue();
                messagesToNuke.clear();
                remaining -= 100;
            } else {
                messagesToNuke.addAll(e.getTextChannel().getHistory().retrievePast(remaining).complete());
                e.getTextChannel().deleteMessages(messagesToNuke).queue();
                messagesToNuke.clear();
                remaining = 0;
            }
        }
    }
}