package dev.zawarudo.holo.core.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public final class MessageReply implements CommandContext.Reply {

    private final @NotNull MessageReceivedEvent event;
    private final @NotNull Message replyTo;

    public MessageReply(@NotNull MessageReceivedEvent event) {
        this(event, event.getMessage());
    }

    public MessageReply(@NotNull MessageReceivedEvent event, @NotNull Message replyTo) {
        this.event = event;
        this.replyTo = replyTo;
    }

    @Override
    public void typing() {
        event.getChannel().sendTyping().queue();
    }

    @Override
    public void text(@NonNull String content) {
        replyTo.reply(content).queue();
    }

    @Override
    public void embed(@NonNull EmbedBuilder embed) {
        replyTo.replyEmbeds(embed.build()).queue();
    }

    @Override
    public void error(@NonNull String content) {
        // TODO: Implement
    }

    public @NotNull MessageReceivedEvent event() {
        return event;
    }

    public @NotNull Message replyTo() {
        return replyTo;
    }
}