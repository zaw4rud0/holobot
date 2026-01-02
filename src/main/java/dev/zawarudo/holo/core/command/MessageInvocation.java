package dev.zawarudo.holo.core.command;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class MessageInvocation implements CommandContext.Invocation {

    private final @NotNull MessageReceivedEvent event;

    public MessageInvocation(@NotNull MessageReceivedEvent event) {
        this.event = event;
    }

    @Override
    public CommandContext.CommandSource source() {
        return CommandContext.CommandSource.MESSAGE;
    }

    @Override
    public User user() {
        return event.getAuthor();
    }

    @Override
    public @Nullable Member member() {
        return event.getMember();
    }

    @Override
    public boolean inGuild() {
        return event.isFromGuild();
    }

    @Override
    public @Nullable Guild guild() {
        return event.isFromGuild() ? event.getGuild() : null;
    }

    @Override
    public MessageChannelUnion channel() {
        return event.getChannel();
    }

    @Override
    public @NotNull Message message() {
        return event.getMessage();
    }
}