package dev.zawarudo.holo.general;

import dev.zawarudo.holo.annotations.Command;
import dev.zawarudo.holo.core.AbstractCommand;
import dev.zawarudo.holo.core.CommandCategory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

@Command(name = "ping",
        description = "Shows the ping of the bot",
        alias = {"pong"},
        category = CommandCategory.GENERAL)
public class PingCmd extends AbstractCommand {

    @Override
    public void onCommand(@NotNull MessageReceivedEvent event) {
        deleteInvoke(event);
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Pong!");
        builder.setDescription("Ping: `...` ms\nHeartbeat: `...` ms");
        long start = System.currentTimeMillis();
        Message message = sendEmbedAndGetMessage(event, builder);
        long ms = System.currentTimeMillis() - start;
        builder.setDescription("Ping: `" + ms + "` ms\nHeartbeat: `" + event.getJDA().getGatewayPing() + "` ms");
        message.editMessageEmbeds(builder.build()).queue();
        message.delete().queueAfter(1, TimeUnit.MINUTES);
    }

    /**
     * Sends a message with the embed and returns it.
     */
    private Message sendEmbedAndGetMessage(@NotNull MessageReceivedEvent e, @NotNull EmbedBuilder builder) {
        if (e.getMember() != null) {
            String text = String.format("Invoked by %s", e.getMember().getEffectiveName());
            builder.setFooter(text, e.getAuthor().getEffectiveAvatarUrl());
        }
        return e.getChannel().sendMessageEmbeds(builder.build()).complete();
    }
}