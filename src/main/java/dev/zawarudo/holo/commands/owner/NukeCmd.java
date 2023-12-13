package dev.zawarudo.holo.commands.owner;

import dev.zawarudo.holo.utils.annotations.Command;
import dev.zawarudo.holo.commands.AbstractCommand;
import dev.zawarudo.holo.commands.CommandCategory;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Command(name = "nuke",
        description = "Deletes a given amount of messages indiscriminately within the channel.",
        usage = "<amount>",
        ownerOnly = true,
        category = CommandCategory.OWNER)
public class NukeCmd extends AbstractCommand {

    @Override
    public void onCommand(@NotNull MessageReceivedEvent e) {
        if (e.getChannel().getType() != ChannelType.TEXT) {
            return;
        }

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

        while (remaining > 0) {
            if (remaining > 100) {
                deleteMessagesFromChannel(e.getChannel().asTextChannel(), 100);
                remaining -= 100;
            } else {
                deleteMessagesFromChannel(e.getChannel().asTextChannel(), remaining);
                remaining = 0;
            }
        }
    }

    /**
     * Deletes a given amount of messages from a channel.
     */
    private void deleteMessagesFromChannel(TextChannel channel, int amount) {
        List<Message> messages = channel.getHistory().retrievePast(amount).complete();
        channel.deleteMessages(messages).queue();
    }
}