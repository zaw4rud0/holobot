package dev.zawarudo.holo.misc;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Represents a submission from a user to the bot owner. This can either
 * be a bug report or a suggestion.
 */
public class Submission {

    private final Type type;
    private final String authorId;
    private final String message;
    private final String date;
    private final String guildId;
    private final String channelId;

    public Submission(Type type, MessageReceivedEvent event, String message) {
        this.type = type;
        this.authorId = event.getAuthor().getId();
        this.message = message;
        this.date = event.getMessage().getTimeCreated().toString();
        this.guildId = event.getGuild().getId();
        this.channelId = event.getChannel().getId();
    }

    public String getType() {
        return switch (type) {
            case SUGGESTION -> "suggestion";
            case BUG -> "bug report";
        };
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    public String getGuildId() {
        return guildId;
    }

    public String getChannelId() {
        return channelId;
    }

    /**
     * The type of submission.
     */
    public enum Type {
        BUG,
        SUGGESTION
    }
}